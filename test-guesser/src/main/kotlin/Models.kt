import Utils.replace
import models.Question
import models.Test

sealed class TestState {
    data class Opened(val test: Test) : TestState()
    data object Closed : TestState()
    data class Ended(val results: Results) : TestState()
}

data class Results(
    val correctAnswers: Int,
    val totalAnswers: Int
)

fun Question.clearAnswers(): Question = when (this) {
    is Question.SingleAnswer -> Question.SingleAnswer(question, answers, null)
    is Question.SeveralAnswer -> Question.SeveralAnswer(question, answers, trueAnswers.map { false })
}

fun Question.changeAnswer(aindex: Int): Question = when (this) {
    is Question.SingleAnswer -> Question.SingleAnswer(question, answers, aindex)
    is Question.SeveralAnswer -> Question.SeveralAnswer(question, answers, trueAnswers.replace(aindex) { !it })
}

infix fun Question.compareTrueAnswers(other: Question): Boolean {
    if (this.javaClass != other.javaClass) return false
    return when (this) {
        is Question.SingleAnswer -> this.trueAnswerIndex == (other as Question.SingleAnswer).trueAnswerIndex
        is Question.SeveralAnswer -> this compareTrueAnswers (other as Question.SeveralAnswer)
    }
}

private infix fun Question.SeveralAnswer.compareTrueAnswers(other: Question.SeveralAnswer): Boolean {
    if (this.trueAnswers.size != other.trueAnswers.size) return false
    this.trueAnswers.forEachIndexed { index, b ->
        if (other.trueAnswers[index] != b) return false
    }
    return true
}