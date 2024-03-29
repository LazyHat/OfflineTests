import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import theme.LazyTheme

val LocalDensity = compositionLocalOf<Density> { error("DENSITY ERROR") }
val LocalMainViewModel = compositionLocalOf<MainViewModel> { error("VIEWMODEL COMPOSITION ERROR") }

@Composable
@Preview
fun App(exit: () -> Unit) {
    MaterialTheme() {
        LazyTheme {
            CompositionLocalProvider(value = LocalDensity provides Density(1f)) {

                Window(
                    onCloseRequest = exit,
                    title = "TestGuesser"
                ) {
                    CompositionLocalProvider(LocalMainViewModel provides MainViewModel()) {
                        MainScreen()
                    }
                }
            }
        }
    }
}

fun main() = application {
    App(::exitApplication)
}
