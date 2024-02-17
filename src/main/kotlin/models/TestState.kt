package models

sealed class TestState {
    data class Opened(val testInfo: TestInfo, val questions: List<QuestionState>) : TestState()
    object Closed : TestState()
}

data class QuestionState(
    val question: Question,
    val editing: Boolean = false
)