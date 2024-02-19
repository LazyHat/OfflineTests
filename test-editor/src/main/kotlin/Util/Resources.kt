package Util

import Utils.BuildConfig
import java.io.File


object R {
    private val resourceDir = if (BuildConfig.debug) File("test-editor/src/main/resources") else File(System.getProperty("compose.application.resources.dir"))

    object drawable {

        val close = resourceDir.resolve("close.svg")

        val delete_forever = resourceDir.resolve("delete_forever.svg")

        val draft = resourceDir.resolve("draft.svg")

        val file_save = resourceDir.resolve("file_save.svg")

        val folder = resourceDir.resolve("folder.svg")

        val save = resourceDir.resolve("save.svg")
    }
}