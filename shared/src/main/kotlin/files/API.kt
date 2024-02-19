package files

import Utils.BuildConfig
import Utils.OS
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Test
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object FileApi {

    private fun getFileLinux(folder: Boolean): File? {
        val commands = listOf("zenity", "--file-selection").let {
            if (folder)
                it + "--save"
            else
                it
        }
        val p = ProcessBuilder(commands).start()
        p.waitFor()
        return if (p.exitValue() == 0) {
            return File(p.inputReader().use { it.readLine() })
        } else null
    }

    private fun getFileUnspecified(folder: Boolean): File? {
        val message = if (folder) "Choose destination" else "Choose file"
        val jfc = JFileChooser()
        jfc.fileFilter = FileNameExtensionFilter("Test File", "test")
        jfc.dialogType = if (folder) JFileChooser.SAVE_DIALOG else JFileChooser.OPEN_DIALOG
        val ret = jfc.showDialog(null, message)
        return if (ret == JFileChooser.APPROVE_OPTION) {
            jfc.selectedFile
        } else null
    }

    fun getFileOS(folder: Boolean) =
        if (BuildConfig.os == OS.Linux) getFileLinux(folder) else getFileUnspecified(folder)

    fun chooseFile(): File? {
        val file = getFileOS(false)
        check(file == null || file.extension == "test") { "INVALID FILE EXTENSION" }
        return file
    }

    fun chooseSaveDestination(): File? {
        return getFileOS(true)?.let {
            if (it.extension != "test") {
                val newFile = File(it.path + ".test")
                it.renameTo(newFile)
                newFile
            } else it
        }
    }

    fun getTestFromFile(file: File): Test {
        if (!file.exists())
            error("FILE DOES NOT EXISTS")
        else
            return try {
                Json.decodeFromString(file.readBytes().decodeToString())
            } catch (e: SerializationException) {
                error("FILE DESERIALIZATION ERROR")
            }
    }

    fun saveTestToFile(
        file: File?,
        test: Test,
        onSuccess: (File) -> Unit,
        onOverwrite: ((save: () -> Unit) -> Unit)?
    ) {
        val encoded = Json.encodeToString(test).encodeToByteArray()

        (file ?: chooseSaveDestination())?.let {
            val l = {
                it.writeBytes(encoded)
                onSuccess(it)
            }

            if (it.exists())
                onOverwrite?.invoke(l) ?: l()
            else {
                it.createNewFile()
                l()
            }
        }
    }
}