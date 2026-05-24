package com.example.tavi.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AppNodeEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TaviDatabase : RoomDatabase() {
    abstract fun appNodeDao(): AppNodeDao

    companion object {
        @Volatile private var INSTANCE: TaviDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE app_nodes ADD COLUMN fossilStatus TEXT DEFAULT NULL")
            }
        }

        fun getInstance(context: Context): TaviDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, TaviDatabase::class.java, "tavi.db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
