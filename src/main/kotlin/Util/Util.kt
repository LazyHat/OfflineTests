package Util

import LocalBuildConfig
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
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

data class BuildConfig(
    val appVersion: String,
    val resourceDir: File,
    val os: String
) {
    companion object {
        val _os = System.getProperty("os.name")

        //depending on whether it is a build or a simple launch from the IDE, you need to select the BuildConfig init function B(Build) or D(Debug)
        fun ProvidableCompositionLocal<BuildConfig>.provide() = this provides initB()

        private fun initB() = BuildConfig(
            appVersion = System.getProperty("jpackage.app-version"),
            resourceDir = File(System.getProperty("compose.application.resources.dir")),
            os = _os
        )

        private fun initD() = BuildConfig(
            appVersion = File("gradle.properties").readText().split('\n').find { it.startsWith("app.version=") }
                ?.substringAfter('=') ?: "NOTHING",
            resourceDir = File("src/main/resources/common"),
            os = _os
        )
    }
}

object R {
    object drawable {

        val close: File
            @Composable
            get() = LocalBuildConfig.current.resourceDir.resolve("close.svg")

        val delete_forever: File
            @Composable
            get() = LocalBuildConfig.current.resourceDir.resolve("delete_forever.svg")

        val draft: File
            @Composable
            get() = LocalBuildConfig.current.resourceDir.resolve("draft.svg")

        val file_save: File
            @Composable
            get() = LocalBuildConfig.current.resourceDir.resolve("file_save.svg")

        val folder: File
            @Composable
            get() = LocalBuildConfig.current.resourceDir.resolve("folder.svg")

        val save: File
            @Composable
            get() = LocalBuildConfig.current.resourceDir.resolve("save.svg")
    }
}