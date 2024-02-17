
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.KoinApplication
import ui.MainScreen
import ui.MainViewModel
import ui.theme.mainColors

val LocalDensity = compositionLocalOf<Density> { error("DENSITY ERROR") }
//val LocalWindowKeyEventHolder = compositionLocalOf<WindowKeyEventHolder> { error("WINDDOW KEY EVENT HOLDER ERROR") }
val LocalMainViewModel = compositionLocalOf<MainViewModel> { error("VIEWMODEL COMPOSITION ERROR") }

@Composable
@Preview
fun App(exit: () -> Unit) {
    MaterialTheme(
        colors = mainColors
    ) {
        CompositionLocalProvider(value = LocalDensity provides Density(1f)) {
            //CompositionLocalProvider(LocalWindowKeyEventHolder provides WindowKeyEventHolder()) {

                Window(
                    onCloseRequest = exit,
                   // onKeyEvent = LocalWindowKeyEventHolder.current.onKeyEvent,
                    title = "TestEditor"
                ) {
                    CompositionLocalProvider(LocalMainViewModel provides MainViewModel()) {
                        MainScreen()
                    }
                }
          //  }
        }
    }
}


fun main() = application {
    KoinApplication(
        {
        }
    ) {
        App(::exitApplication)
    }
}





