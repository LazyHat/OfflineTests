package models

private fun Question.SingleAnswer.copy(
    question: String = this.question,
    answers: List<String> = this.answers,
    trueAnswerIndex: Int? = this.trueAnswerIndex
): Question.SingleAnswer =
    Question.SingleAnswer(question, answers, trueAnswerIndex)

private fun Question.SeveralAnswer.copy(
    question: String = this.question,
    answers: List<String> = this.answers,
    trueAnswers: List<Boolean> = this.trueAnswers
): Question.SeveralAnswer =
    Question.SeveralAnswer(question, answers, trueAnswers)

fun Question.copyQuestion(question: String = this.question): Question = when (this) {
    is Question.SingleAnswer -> Question.SingleAnswer(question, answers, trueAnswerIndex)
    is Question.SeveralAnswer -> Question.SeveralAnswer(question, answers, trueAnswers)
}

fun Question.addAnswer(answer: String): Question = when (this) {
    is Question.SingleAnswer -> this.copy(answers = this.answers + answer)
    is Question.SeveralAnswer -> this.copy(answers = this.answers + answer, trueAnswers = this.trueAnswers + false)
}

fun Question.removeAnswer(index: Int): Question = when (this) {
    is Question.SingleAnswer -> this.copy(answers = this.answers.filterIndexed { indexm, _ -> index != indexm })
    is Question.SeveralAnswer -> {
        val predicate = { indexp: Int, _: Any -> indexp != index }
        this.copy(
            answers = this.answers.filterIndexed(predicate),
            trueAnswers = this.trueAnswers.filterIndexed(predicate)
        )
    }
}

fun Question.editAnswer(index: Int, new: String): Question = when (this) {
    is Question.SingleAnswer -> this.copy(answers = this.answers.mapIndexed { indexm, b -> if (index == indexm) new else b })
    is Question.SeveralAnswer -> this.copy(answers = this.answers.mapIndexed { indexm, b -> if (index == indexm) new else b })
}

fun Question.SingleAnswer.editTrueAnswerIndex(new: Int): Question.SingleAnswer = this.copy(trueAnswerIndex = new)

fun Question.SeveralAnswer.changeTrueAnswer(index: Int) =
    this.copy(trueAnswers = this.trueAnswers.mapIndexed { indexm, b -> if (index == indexm) !b else b })

fun Question.castToSingleAnswer() = Question.SingleAnswer.Default.copy(question, answers)
fun Question.castToSeveralAnswer() =
    Question.SeveralAnswer.Default.copy(question, answers, List(answers.size) { false })