import Utils.replace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.hoc081098.kmp.viewmodel.ViewModel
import files.FileApi
import kotlinx.coroutines.flow.*
import models.Question
import models.Test

class MainViewModel : ViewModel() {
    private val _test = MutableStateFlow<Test?>(null)

    private val _answers = MutableStateFlow<List<Question>?>(null)

    private val _results = MutableStateFlow<Results?>(null)

    private val _state = combine(_test, _answers, _results) { test, answers, results ->
        if (results != null)
            TestState.Ended(results)
        else if (test == null || answers == null)
            TestState.Closed
        else
            TestState.Opened(Test(test.info, answers))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TestState.Closed)

    @Composable
    fun collectTestStateAsState() = _state.collectAsState()

    fun endTest() {
        val test = _test.value
        check(test != null)
        val answers = _answers.value
        check(answers != null)
        _results.update { Results(getCorrectAnswers(answers, test.questions), answers.size) }
    }

    private fun getCorrectAnswers(answers: List<Question>, test: List<Question>): Int {
        return answers.foldIndexed(0) { index, acc, it ->
            acc + if (test[index] compareTrueAnswers  it) 1 else 0
        }
    }

    fun openTest() {
        val file = FileApi.chooseFile()
        file?.let {
            val test = FileApi.getTestFromFile(file)
            _test.value = test
            _answers.value = test.questions.map { it.clearAnswers() }
        }
    }

    fun changeAnswer(qindex: Int, aindex: Int) {
        _answers.update {
            check(it != null)
            it.replace(qindex) {
                it.changeAnswer(aindex)
            }
        }
    }
}