package com.example.socialmedia.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.socialmedia.model.Level


@Dao
interface LevelDAO : BaseDAO<Level> {
     @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insertLevel(level: Level)

     @Query("SELECT * FROM Level")
     fun getAllLevel() : List<Level>

     @Query("UPDATE Level set numberQuestionDone =:count where level =:level")
     fun update(count : String, level : String)

//     @Query("update Level set numberQuestionDone =:done where level =:level and id =:id")
//     fun update(done : String, level : String, id : Int)
}