package com.bubul.col.launcher

import com.bubul.col.launcher.core.ext.delete
import com.bubul.col.launcher.core.ext.exists
import com.bubul.col.launcher.core.startTmpLauncher
import com.bubul.col.launcher.ui.UiManager
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

private const val GAME_UPDATE_URL : String = "https://cardsoflegendsupdate.bubul.ovh/"

private const val UPDATE_LAUNCHER = true

fun main() {
    val logger = LoggerFactory.getLogger("Main")
    val elementUpdater = ElementUpdater()
    val currentPath = Paths.get(File("").absolutePath)
    logger.info("Current path : {}", currentPath.toString())
    if(currentPath.parent.fileName.toString() == "tmp")
    {
        logger.info("Run copy")
        elementUpdater.runCopy()
        exitProcess(0)
    }
    else
    {
        val tmpLauncherPath = Paths.get(Paths.get(File("").absolutePath).parent.toAbsolutePath().toString(), "/tmp/launcher")
        if(tmpLauncherPath.exists()) {
            logger.info("Delete tmp directory")
            Files.walk(tmpLauncherPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete)
        }
    }

    val uiManager = UiManager()
    uiManager.startUi()
    uiManager.waitReady()
    if (UPDATE_LAUNCHER)
        if (elementUpdater.runUpdate(GAME_UPDATE_URL, "launcher", uiManager.getUpdateManagerListener("launcher")) > 0)
        {
            startTmpLauncher()
            uiManager.exit()
        }
    elementUpdater.runUpdate(GAME_UPDATE_URL, "game", uiManager.getUpdateManagerListener("game"))

}