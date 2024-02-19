package Utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.unit.Density
import java.io.File
import kotlin.math.max
import kotlin.math.min

@Composable
fun svgPainterResource(resource: File, density: Density = LocalDensity.current): Painter =
    remember { resource.inputStream().use { loadSvgPainter(it, density) } }

fun Color.increaseRGB(increaseBy: Float) =
    this.copy(
        red = treshold(this.red + increaseBy, 0f, 1f),
        green = treshold(this.green + increaseBy, 0f, 1f),
        blue = treshold(this.blue + increaseBy, 0f, 1f)
    )

fun treshold(x: Float, min: Float, max: Float) = max(min(x, max), min)

fun <T> List<T>.replace(index: Int, replace: (T) -> T) =
    this.mapIndexed { mindex, it -> if (mindex == index) replace(it) else it }