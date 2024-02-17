package Util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun svgPainterResource(resource: File, density: Density = LocalDensity.current): Painter =
    remember { resource.inputStream().use { loadSvgPainter(it, density) } }

@Composable
fun Dialog(
    question: String,
    text1: String,
    text2: String,
    text3: String?,
    action1: () -> Unit,
    action2: () -> Unit,
    action3: (() -> Unit)?,
) {
    Card(Modifier.width(400.dp).height(200.dp), border = BorderStroke(width = 3.dp, Color.Red)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(question)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(action1) { Text(text1) }
                Button(action2) { Text(text2) }
                if (action3 != null && text3 != null)
                    Button(action3) { Text(text3) }
            }
        }
    }
}

//fun <T> List<T>.replace(index: Int, replace: (T) -> T) =
//    this.mapIndexed { mindex, it -> if (mindex == index) replace(it) else it }

enum class OS {
    Linux,
    Windows,
    Unspecified
}


object BuildConfig {

    //depending on whether it is a build or a simple launch from the IDE, you need to select this var to true or false
    private const val debug = true

    val appVersion: String =
        if (debug) File("gradle.properties").readText().split('\n').find { it.startsWith("app.version=") }
            ?.substringAfter('=') ?: "NOTHING" else System.getProperty("jpackage.app-version")
    val resourceDir: File =
        if (debug) File("src/main/resources/common") else File(System.getProperty("compose.application.resources.dir"))

    val os = try {
        OS.valueOf(System.getProperty("os.name"))
    } catch (e: IllegalStateException) {
        OS.Unspecified
    }
}

object R {
    object drawable {

        val close = BuildConfig.resourceDir.resolve("close.svg")

        val delete_forever = BuildConfig.resourceDir.resolve("delete_forever.svg")

        val draft = BuildConfig.resourceDir.resolve("draft.svg")

        val file_save = BuildConfig.resourceDir.resolve("file_save.svg")

        val folder = BuildConfig.resourceDir.resolve("folder.svg")

        val save = BuildConfig.resourceDir.resolve("save.svg")
    }
}