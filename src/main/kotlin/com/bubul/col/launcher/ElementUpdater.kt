package com.bubul.col.launcher

import com.bubul.col.launcher.core.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.copyTo

class ElementUpdater {

    fun runUpdate(url : String, type : String, listener : UpdateManagerListener) : Int
    {
        val downloadBasePath = getPathForType(type, "download")
        val bddBasePath = getPathForType(type, "database")
        val downloadManager = DownloadManager()
        val updateFetcher = UpdateFetcher(url, downloadBasePath)
        updateFetcher.init(bddBasePath)
        val updateManager = UpdateManager(downloadManager, updateFetcher, listener)
        val res = updateManager.doUpdate(type)
        return res
    }

    fun runCopy()
    {
        val tmpLauncherPath = Paths.get(File("").absolutePath)
        val LaucherPath = Paths.get(tmpLauncherPath.parent.parent.toString(), tmpLauncherPath.fileName.toString())
        tmpLauncherPath.toFile().copyRecursively(LaucherPath.toFile(), true)
        startLauncherFromTmp()
    }

    fun getPathForType(type : String, kind : String) : String
    {
        var res = File("").absolutePath+"/../"
        if(type == "launcher" && kind == "download")
        {
            res = res +"/tmp/"
        }
        res = res+type+"/"
        return res
    }
}