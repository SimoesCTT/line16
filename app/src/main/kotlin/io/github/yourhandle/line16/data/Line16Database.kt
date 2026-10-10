package io.github.yourhandle.line16.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReturnEntity::class], version = 2, exportSchema = false)
abstract class Line16Database : RoomDatabase() {
    abstract fun returnDao(): ReturnDao

    companion object {
        @Volatile private var instance: Line16Database? = null

        fun get(context: Context): Line16Database =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    Line16Database::class.java,
                    "line16.db",
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
