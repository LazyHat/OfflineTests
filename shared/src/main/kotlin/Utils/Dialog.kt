package Utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import theme.LazyTheme

class DialogStateHolder() {

    private val state = MutableStateFlow<DialogState>(DialogState.Closed)

    @Composable
    fun collectDialogState() = state.collectAsState()

    sealed class DialogState private constructor() {
        data class Opened(
            val question: String,
            val text1: String,
            val text2: String?,
            val text3: String?,
            val action1: () -> Unit,
            val action2: (() -> Unit)?,
            val action3: (() -> Unit)?
        ) : DialogState()

        data object Closed : DialogState()
    }

    fun showDialog(
        question: String,
        text1: String,
        text2: String?,
        text3: String?,
        action1: () -> Unit,
        action2: (() -> Unit)?,
        action3: (() -> Unit)?
    ) {
        state.update {
            DialogState.Opened(
                question,
                text1,
                text2,
                text3,
                {
                    state.update { DialogState.Closed }
                    action1.invoke()
                },
                action2?.let {
                    {
                        state.update { DialogState.Closed }
                        it.invoke()
                    }
                },
                action3?.let {
                    {
                        state.update { DialogState.Closed }
                        it.invoke()
                    }
                })
        }
    }

    fun showErrorDialog(question: String) {
        state.update {
            DialogState.Opened(
                question,
                "OK",
                null,
                null,
                {
                    state.update { DialogState.Closed }
                },
                null,
                null
            )
        }
    }

    fun showYesNoDialog(question: String, onYes: () -> Unit, onNo: () -> Unit) {
        state.update {
            DialogState.Opened(
                question,
                "YES",
                "NO",
                null,
                {
                    state.update { DialogState.Closed }
                    onYes()
                },
                {
                    state.update { DialogState.Closed }
                    onNo()
                },
                null
            )
        }
    }
}


@Composable
fun Dialog(
    state: DialogStateHolder.DialogState
) {
    if (state is DialogStateHolder.DialogState.Opened) {
        val buttonColors = ButtonDefaults.buttonColors(
            backgroundColor = LazyTheme.colors.primary,
            contentColor = LazyTheme.colors.onPrimary
        )

        Card(
            Modifier.width(400.dp).height(200.dp),
            border = BorderStroke(width = 3.dp, Color.Red),
            backgroundColor = LazyTheme.colors.barOnBackground,
            elevation = 10.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(state.question, color = LazyTheme.colors.onBackground)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(state.action1, colors = buttonColors) { Text(state.text1) }
                    if (state.action2 != null && state.text2 != null)
                        Button(state.action2, colors = buttonColors) { Text(state.text2) }
                    if (state.action3 != null && state.text3 != null)
                        Button(state.action3, colors = buttonColors) { Text(state.text3) }
                }
            }
        }
    }
}