package com.bubul.col.launcher.core

import com.bubul.col.launcher.core.ext.exists
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.sql.Connection
import kotlin.io.path.createDirectories

object DataVersioneds : IntIdTable() {
    val name = varchar("name", 250)
    val size = integer("size")
    val date = varchar("date", 19) // YYYY-MM-DD HH:MM:SS
}

class DataVersioned(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DataVersioned>(DataVersioneds)
    var name     by DataVersioneds.name
    var size by DataVersioneds.size
    var date by DataVersioneds.date
}

class LocalDataManager {

    private lateinit var db : Database
    private val logger = LoggerFactory.getLogger( this.javaClass.name)
    private var localDataAvailiable = false

    fun init(basePath : String)
    {
        val dbPath = Paths.get(basePath,"dataversioning.db")
        dbPath.parent.createDirectories()
        localDataAvailiable = dbPath.exists()
        db = Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")
        if (!dbPath.exists())
        {
            transaction {
                SchemaUtils.create(DataVersioneds)
            }
        }
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
    }

    fun isLocalDataAvailiable() : Boolean
    {
        return localDataAvailiable
    }

    fun create(aName : String, aSize : Int, aDate : String)
    {
        logger.info("Updating local database : create : {} ", aName)
        transaction {
            DataVersioned.new {
                name = aName
                size = aSize
                date = aDate
            }
        }
    }

    fun delete(aName : String)
    {
        logger.info("Updating local database : delete : {} ", aName)
        transaction {
            for (data in DataVersioned.find { DataVersioneds.name eq aName })
            {
                data.delete()
            }
        }
    }

    fun update(aName : String, aSize : Int, aDate : String)
    {
        logger.info("Updating local database : update : {} ", aName)
        transaction {
            for (data in DataVersioned.find { DataVersioneds.name eq aName })
            {
                data.size = aSize
                data.date = aDate
            }
        }
    }

    fun fetchAll(): List<DataVersioned> {
        logger.info("Fetching local database")
        val res = mutableListOf<DataVersioned>()
        transaction {
            DataVersioned.all().forEach {
                dataVersioned -> res.add(dataVersioned)
            }
        }
        return res
    }

}