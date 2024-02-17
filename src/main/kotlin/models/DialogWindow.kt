package models

sealed class DialogWindow {
    data class Opened(
        val question: String,
        val text1: String,
        val text2: String,
        val text3: String?,
        val action1: () -> Unit,
        val action2: () -> Unit,
        val action3: (() -> Unit)?
    ) : DialogWindow()

    object Closed : DialogWindow()
}