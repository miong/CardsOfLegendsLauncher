package com.bubul.col.launcher.core

import org.kamranzafar.jddl.DirectDownloader
import org.kamranzafar.jddl.DownloadListener
import org.kamranzafar.jddl.DownloadTask
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

open class DownloadManagerListener {
    open fun onStart(fname: String?, size: Int) {}
    open fun onUpdate(bytes: Int, totalDownloaded: Int) {}
    open fun onComplete() {}
    open fun onCancel() {}
}

class DownloadManager {

    private val dd = DirectDownloader()
    private val downloads = mutableMapOf<String, String>()
    private val logger = LoggerFactory.getLogger( this.javaClass.name)

    fun addDownload(url : String, file : String, listener: DownloadManagerListener)
    {
        downloads[file] = "unstarted"
        logger.info("Adding download file : {}",file)
        val inURL = URL(url)
        Files.createDirectories(Paths.get(file).parent)
        val outFile = FileOutputStream(file)
        val ddl = object : DownloadListener {
            private var fileName : String = "Unset"

            override fun onStart(fname: String?, size: Int) {
                logger.info("Staring download file : {}", file)
                fileName = file
                downloads[file] = "started"
                listener.onStart(fname, size)
            }

            override fun onUpdate(bytes: Int, totalDownloaded: Int) {
                listener.onUpdate(bytes, totalDownloaded)
            }

            override fun onComplete() {
                logger.info("Completed download file : {}",file)
                downloads[file] = "finished"
                listener.onComplete()
            }

            override fun onCancel() {
                downloads[file] = "cancelled"
                listener.onCancel()
            }
        }
        val dt = DownloadTask(inURL , outFile, ddl)
        dd.download(dt)
    }

    fun performDownloads()
    {
        val t  = Thread( dd )
        t.start()
        t.join()
        while (downloads.values.contains("unstarted") || downloads.values.contains("started")) {
            Thread.sleep(500)
        }
    }
}