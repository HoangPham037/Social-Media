package com.example.socialmedia.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.socialmedia.model.QuestionLevel
import com.example.socialmedia.model.Level


@Database(entities = [Level::class, QuestionLevel::class], version = 1, exportSchema = false)
@TypeConverters(QuestionLevelConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun levelDAO() : LevelDAO
    abstract fun questionLevelDAO() : QuestionLevelDAO
    companion object{
        private var db_instance : AppDatabase?= null

        fun getAppDatabaseInstance(context: Context) : AppDatabase {
            if (db_instance == null){
                db_instance = Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java,
                "level")
                    .allowMainThreadQueries()
                    .build()
            }
            return db_instance!!
        }
    }
}