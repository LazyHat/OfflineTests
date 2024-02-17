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
        val trueAnswerIndex: Int
    ) : Question {
        companion object {
            val Default = SingleAnswer("QuestTest", listOf("Answer 1"), 0)
        }

        override fun copyQuestion(question: String): Question = this.copy(question = question)

        override fun editAnswer(
            index: Int,
            new: String
        ): Question = this.copy(answers = this.answers.mapIndexed { indexm, b -> if (index == indexm) new else b })

        override fun addAnswer(answer: String): Question = this.copy(answers = this.answers + answer)

        override fun removeAnswer(index: Int): Question =
            this.copy(answers = this.answers.filterIndexed { indexm, _ -> index != indexm })

        fun editTrueAnswerIndex(new: Int): SingleAnswer = this.copy(trueAnswerIndex = new)

        private fun copy(
            question: String = this.question,
            answers: List<String> = this.answers,
            trueAnswerIndex: Int = this.trueAnswerIndex
        ): SingleAnswer =
            SingleAnswer(question, answers, trueAnswerIndex)
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
            val Default = SeveralAnswer("QuestTest", listOf("Answer 1"), listOf(true))
        }

        override fun copyQuestion(question: String): Question = this.copy(question = question)

        override fun editAnswer(
            index: Int,
            new: String
        ): Question = this.copy(answers = this.answers.mapIndexed { indexm, b -> if (index == indexm) new else b })

        override fun addAnswer(answer: String): Question =
            this.copy(answers = this.answers + answer, trueAnswers = this.trueAnswers + false)

        override fun removeAnswer(index: Int): Question {
            val predicate = { indexp: Int, _: Any -> indexp != index }
            return this.copy(
                answers = this.answers.filterIndexed(predicate),
                trueAnswers = this.trueAnswers.filterIndexed(predicate)
            )
        }

        fun changeTrueAnswer(index: Int) =
            this.copy(trueAnswers = this.trueAnswers.mapIndexed { indexm, b -> if (index == indexm) !b else b })

        private fun copy(
            question: String = this.question,
            answers: List<String> = this.answers,
            trueAnswers: List<Boolean> = this.trueAnswers
        ): SeveralAnswer =
            SeveralAnswer(question, answers, trueAnswers)
    }

    fun copyQuestion(question: String = this.question): Question
    fun addAnswer(answer: String): Question
    fun removeAnswer(index: Int): Question
    fun editAnswer(index: Int, new: String): Question
}
