package com.bubul.col.launcher.core

import com.bubul.col.launcher.core.ext.delete
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicLong
import kotlin.io.path.moveTo

interface UpdateManagerListener {
    fun onUpdateStart(isInstallation : Boolean)
    fun onVersioningStart()
    fun onCopyingStart()
    fun onFileStart(fname : String)
    fun onUpdateProgress(percent : Int)
    fun onUpdateEnd()
    fun onCancel()
}

class UpdateManager (private var downloader : DownloadManager, private var fetcher : UpdateFetcher, var listener : UpdateManagerListener)
{
    var updateSize = AtomicLong(0)
    var updateDownloaded = AtomicLong(0)

    fun doUpdate(type : String) : Int
    {
        listener.onUpdateStart(fetcher.isNewInstallation())
        updateSize = AtomicLong(0)
        updateDownloaded = AtomicLong(0)
        val updates = fetcher.calculateUpdates(type)
        for (desc : UpdateFileDescriptor in updates)
        {
            when(desc.type)
            {
                UpdateType.DELETE -> removeFile(desc)
                else -> addDownloadTask(desc)
            }

        }
        downloader.performDownloads()
        listener.onUpdateProgress(100)
        listener.onUpdateProgress(0)
        listener.onVersioningStart()
        var registeredCount = 0
        for (desc : UpdateFileDescriptor in updates) {
            listener.onFileStart(Paths.get(desc.file).fileName.toString())
            fetcher.registerUpdate(desc)
            registeredCount++
            val percents = registeredCount.toFloat() / updates.size.toFloat() * 100
            listener.onUpdateProgress(percents.toInt())
        }
        listener.onUpdateProgress(100)
        listener.onUpdateEnd()
        return updates.size
    }

    private fun removeFile(desc: UpdateFileDescriptor) {
        val p = Paths.get(desc.pathBase+desc.file)
        p.delete()
    }

    private fun addDownloadTask(desc : UpdateFileDescriptor)
    {
        val inURL = desc.urlBase+desc.file
        val outFile = desc.pathBase+desc.file
        updateSize.addAndGet(desc.size.toLong())
        downloader.addDownload(inURL, outFile, object : DownloadManagerListener() {
            override fun onStart(fname: String?, size: Int) {
                listener.onFileStart(fname!!)
            }
            override fun onUpdate(bytes: Int, totalDownloaded: Int) {
                val newValue = updateDownloaded.addAndGet(bytes.toLong()).toFloat() / updateSize.get().toFloat() * 100
                listener.onUpdateProgress(newValue.toInt())
            }
            override fun onComplete() {}
            override fun onCancel() {
                listener.onCancel()
            }
        })
    }
}