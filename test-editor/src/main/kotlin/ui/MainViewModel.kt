package ui

import Utils.DialogStateHolder
import Utils.replace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.hoc081098.kmp.viewmodel.ViewModel
import files.FileApi
import files.FileApi.chooseFile
import files.FileApi.getTestFromFile
import kotlinx.coroutines.flow.*
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


    private val _dialogState = DialogStateHolder()

    @Composable
    fun collectDialogState() = _dialogState.collectDialogState()

    @Composable
    fun testStateAsState() = _memoryTestState.collectAsState()

    @Composable
    fun openedFileNameAsState() =
        _file.map { it?.name }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null).collectAsState()

    fun editTestInfo(new: TestInfo) = _memoryTest.update {
        check(it != null)
        it.copy(info = new)
    }

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
            it.copy(questions = it.questions.replace(index) { new })
        }

    fun changeType(index: Int) = _memoryTest.update {
        check(it != null)
        it.copy(questions = it.questions.replace(index) {
            when (it) {
                is Question.SingleAnswer -> it.castToSeveralAnswer()
                is Question.SeveralAnswer -> it.castToSingleAnswer()
            }
        })
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
            if (test.questions.all { it.checkIfOneAnswerSetted() }) {
                FileApi.saveTestToFile(_file.value, test, {}, {
                    showOverwriteDialog(_file.value?.name.orEmpty(), it)
                })
            } else _dialogState.showErrorDialog("Not all questions filled")
        }
    }

    fun saveTest() {
        _memoryTest.value.let { test ->
            check(test != null)
            check(_file.value != null)
            if (test.questions.all { it.checkIfOneAnswerSetted() }) {
                FileApi.saveTestToFile(_file.value, test, {}, null)
            } else _dialogState.showErrorDialog("Not all questions filled")
        }
    }

    fun openTest() {
        val file = chooseFile()
        file?.let {
            _memoryTest.value = getTestFromFile(file)
            _file.value = file
        }
    }

    fun deleteTest() {
        _file.value.let { file ->
            check(file != null)
            showDeleteDialog(file.name) {
                file.delete()
                _file.value = null
                _memoryTest.value = null
                _editingQuestionIndex.value = null
            }
        }
    }

    fun showOverwriteDialog(filename: String, onYes: () -> Unit) {
        _dialogState.showYesNoDialog("are you sure you want to overwrite the file $filename?", onYes, {})
    }

    fun showDeleteDialog(filename: String, onYes: () -> Unit) {
        _dialogState.showYesNoDialog("are you sure you want to delete the file $filename?", onYes, {})
    }
}

