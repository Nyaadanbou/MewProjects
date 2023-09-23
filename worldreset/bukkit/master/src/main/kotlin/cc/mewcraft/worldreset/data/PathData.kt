package cc.mewcraft.worldreset.data

import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.io.path.Path

class PathData(
    private val plugin: Plugin,
    paths: List<String>,
) {
    private val files: List<File> = paths.map { Path(it).toFile() }

    /**
     * Deletes files specified by [files].
     */
    fun deleteFiles() { // TODO
        files.forEach { plugin.slF4JLogger.info("Deleting file: ${it.path}") }
    }
}
