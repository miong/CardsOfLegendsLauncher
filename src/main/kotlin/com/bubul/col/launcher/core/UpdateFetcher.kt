package com.bubul.col.launcher.core

import java.text.SimpleDateFormat

enum class UpdateType {
    CREATE,
    UPDATE,
    DELETE
}

data class UpdateFileDescriptor(var urlBase : String, var pathBase : String, var file : String, var size : Int, var date : String, var type : UpdateType )


class UpdateFetcher(private var urlBase : String, private var pathBase : String)
{

    private val localDataManager = LocalDataManager()
    private val remoteDataManager = RemoteDataManager(urlBase)

    fun init(basePath : String)
    {
        localDataManager.init(basePath)
        remoteDataManager.init()
    }

    fun isNewInstallation() : Boolean
    {
        return !localDataManager.isLocalDataAvailiable()
    }

    fun calculateUpdates(type : String) : List<UpdateFileDescriptor>
    {
        val res: MutableList<UpdateFileDescriptor> = mutableListOf()
        val localData = localDataManager.fetchAll()
        val remoteData = remoteDataManager.fetchAll(type)
        val remoteUrl = urlBase+"files/"+type+"/"
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        if (remoteData.isEmpty()) {
            return res
        }
        for (rdata in remoteData) {
            var found = false
            for(data in localData)
            {
                if(rdata.name == data.name)
                {
                    found = true
                    val rDate = sdf.parse(rdata.date)
                    val date = sdf.parse(data.date)
                    if(rDate.after(date))
                    {
                        res.add(UpdateFileDescriptor(remoteUrl,pathBase,rdata.name,rdata.size,rdata.date, UpdateType.UPDATE))
                    }
                    break
                }
            }
            if(!found)
            {
                res.add(UpdateFileDescriptor(remoteUrl,pathBase,rdata.name,rdata.size,rdata.date, UpdateType.CREATE))
            }
        }
        for (data in localData) {
            var found = false
            for(rdata in remoteData)
            {
                if(rdata.name == data.name)
                {
                    found = true
                    break
                }
            }
            if(!found)
            {
                res.add(UpdateFileDescriptor(remoteUrl,pathBase,data.name,data.size,data.date, UpdateType.DELETE))
            }
        }
        return res
    }

    fun registerUpdate(update : UpdateFileDescriptor)
    {
        when(update.type)
        {
            UpdateType.CREATE -> localDataManager.create(update.file, update.size, update.date)
            UpdateType.UPDATE -> localDataManager.update(update.file, update.size, update.date)
            UpdateType.DELETE -> localDataManager.delete(update.file)
        }
    }
}