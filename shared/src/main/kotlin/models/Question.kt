package models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed interface Question {
    val question: String
    val answers: List<String>

    @Serializable
    @SerialName("SingleAnswer")
    @Immutable
    class SingleAnswer(
        override val question: String,
        override val answers: List<String>,
        val trueAnswerIndex: Int?
    ) : Question {
        companion object {
            val Default = SingleAnswer("QuestTest", listOf("Answer 1"), null)
        }
    }

    @Serializable
    @SerialName("SeveralAnswer")
    @Immutable
    class SeveralAnswer(
        override val question: String,
        override val answers: List<String>,
        val trueAnswers: List<Boolean>
    ) : Question {
        init {
            check(answers.size == trueAnswers.size) { "ANSWERS SIZE NOT EQUAL TRUE_ANSWERS SIZE" }
        }

        companion object {
            val Default = SeveralAnswer("QuestTest", listOf("Answer 1"), listOf(false))
        }
    }
}

fun Question.checkIfOneAnswerSetted(): Boolean = when (this) {
    is Question.SingleAnswer -> trueAnswerIndex != null
    is Question.SeveralAnswer -> trueAnswers.any { it }
}