package theme

import Utils.increaseRGB
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalTheme = compositionLocalOf<ThemeColors> { error("LOCAL THEME ERROR") }

val mainTheme = ThemeColors(
    background = Color(0xFF101015),
    onBackground = Color.White,
    barOnBackground = Color(0xFF202020),
    topBarIcons = Color.White,
    disabledTopBarIcons = Color.White.increaseRGB(-0.5f),
    primary = Color.Blue,
    onPrimary = Color.White,
    questionBackground = Color(0xFF202020),
    onQuestionBackground = Color.White,
    disabledOnQuestionBackground = Color.White.increaseRGB(-0.5f),
    border = Color.Gray
)

@Stable
@Immutable
data class ThemeColors(
    val background: Color,
    val onBackground: Color,
    val barOnBackground: Color,
    val topBarIcons: Color,
    val disabledTopBarIcons: Color,
    val primary: Color,
    val onPrimary: Color,
    val questionBackground: Color,
    val onQuestionBackground: Color,
    val disabledOnQuestionBackground: Color,
    val border: Color,
)