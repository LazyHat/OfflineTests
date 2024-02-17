package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.hoc081098.kmp.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.*
import java.io.File

class MainViewModel : ViewModel() {
    private val _file = MutableStateFlow<File?>(null)

    private val _memoryTest = MutableStateFlow<Test?>(null)

    private val _editingQuestionIndex = MutableStateFlow<Int?>(null)

    private val _memoryTestState = combine(_memoryTest, _editingQuestionIndex) { test, editQIndex ->
        test?.let {
            TestState.Opened(
                it.info,
                it.questions.mapIndexed { index, question ->
                    QuestionState(question, index == editQIndex)
                }
            )
        } ?: TestState.Closed
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TestState.Closed)


    private val _dialogWindow = MutableStateFlow<DialogWindow>(DialogWindow.Closed)

    @Composable
    fun dialogWindowStateAsState() = _dialogWindow.collectAsState()

    @Composable
    fun testStateAsState() = _memoryTestState.collectAsState()

    @Composable
    fun openedFileNameAsState() =
        _file.map { it?.name }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null).collectAsState()

    fun editTestInfo(new: TestInfo) = _memoryTest.update {
        check(it != null)
        it.copy(info = new)
    }

//    fun stopEditingAll() {
//        _editingQuestionIndex.value = null
//    }

    fun requestEdit(index: Int) {
        _editingQuestionIndex.value = index
    }

    fun addQuestion(question: Question) = _memoryTest.update {
        check(it != null)
        it.copy(questions = it.questions + question)
    }

    fun removeQuestion(index: Int) = _memoryTest.update {
        check(it != null)
        it.copy(questions = it.questions.filterIndexed { indexp, _ -> indexp != index })
    }

    fun editQuestion(index: Int, new: Question) =
        _memoryTest.update {
            check(it != null)
            it.copy(questions = it.questions.mapIndexed { indexm, it1 -> if (index == indexm) new else it1 })
        }

    fun newTest() {
        _memoryTest.update {
            check(it == null)
            Test.Default
        }
    }

    fun closeTest() {
        _memoryTest.value = null
        _file.value = null
    }

    fun saveTestToFile() {
        _editingQuestionIndex.value = null
        _memoryTest.value.let { test ->
            check(test != null)
            val file = chooseSaveDestination()
            file?.let {
                saveTestToFile(file, test)
            }
        }
    }

    fun saveTest() {
        _memoryTest.value.let { test ->
            check(test != null)
            val file = _file.value ?: chooseSaveDestination()
            file?.let {
                saveTestToFile(file, test, true)
            }
        }
    }

    fun openTest() {
        val file = chooseFile()
        file?.let {
            _memoryTest.value = getTestFromFile(file)
            _file.value = file
        }
    }

    private fun chooseFile(): File? {
        val p = ProcessBuilder("zenity", "--file-selection", "--filename=*.test").start()
        p.waitFor()
        return if (p.exitValue() == 0) {
            val path = p.inputReader().use { it.readLine() }.let {
                if (!it.endsWith(".test"))
                    error("INVALID FILE EXTENSION")
                else it
            }
            File(path)
        } else null
    }

    private fun getTestFromFile(file: File): Test {
        if (!file.exists())
            error("FILE DOES NOT EXISTS")
        else
            return try {
                Json.decodeFromString(file.readBytes().decodeToString())
            } catch (e: SerializationException) {
                error("FILE DESERIALIZATION ERROR")
            }
    }

    private fun chooseSaveDestination(): File? {
        val p = ProcessBuilder("zenity", "--file-selection", "--save").start()
        p.waitFor()
        return if (p.exitValue() == 0) {
            val path = p.inputReader().use { it.readLine() }.let {
                if (!it.endsWith(".test"))
                    "$it.test"
                else it
            }
            File(path)
        } else null

    }

    private fun saveTestToFile(file: File, test: Test, owerwriteBypass: Boolean = false) {
        if (!file.exists()) {
            file.createNewFile()
            file.writeBytes(Json.encodeToString(test).encodeToByteArray())
            if (_file.value == null)
                _file.value = file
        } else if (owerwriteBypass) {
            file.writeBytes(Json.encodeToString(test).encodeToByteArray())
            if (_file.value == null)
                _file.value = file
        } else {
            _dialogWindow.value = DialogWindow.Opened(
                "are you sure you want to overwrite the file ${file.name}",
                "yes",
                "no",
                null,
                {
                    file.writeBytes(Json.encodeToString(test).encodeToByteArray())
                    _dialogWindow.value = DialogWindow.Closed
                    if (_file.value == null)
                        _file.value = file
                },
                {
                    _dialogWindow.value = DialogWindow.Closed
                },
                null
            )
        }
    }

    fun deleteTest() {
        _file.value.let { file ->
            check(file != null)
            _dialogWindow.value = DialogWindow.Opened(
                "are you sure you want to delete the file ${file.name}",
                "yes",
                "no",
                null,
                {
                    file.delete()
                    _file.value = null
                    _memoryTest.value = null
                    _editingQuestionIndex.value = null
                    _dialogWindow.value = DialogWindow.Closed

                },
                {
                    _dialogWindow.value = DialogWindow.Closed
                },
                null
            )
        }
    }
}

