package Utils

import java.io.File

enum class OS {
    Linux,
    Windows,
    Unspecified
}

object BuildConfig {

    //depending on whether it is a build or a simple launch from the IDE, you need to select this var to true or false
    const val debug = false

    val appVersion: String =
        if (debug) File("gradle.properties").readText().split('\n').find { it.startsWith("app.version=") }
            ?.substringAfter('=') ?: "NOTHING" else System.getProperty("jpackage.app-version")

    val os = try {
        OS.valueOf(System.getProperty("os.name").substringBefore(' '))
    } catch (e: IllegalStateException) {
        OS.Unspecified
    }
}