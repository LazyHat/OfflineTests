package theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun LazyTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalTheme provides mainTheme, content)
}

object LazyTheme {
    val colors: ThemeColors
        @Composable
        get() = LocalTheme.current
}