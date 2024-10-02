package com.example.socialmedia.database

import androidx.room.TypeConverter
import com.example.socialmedia.model.QuestionLevel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QuestionLevelConverter {

    @TypeConverter
    fun fromQuestionLevelList(value : List<QuestionLevel>) : String{
        val gson = Gson()
        val type = object : TypeToken<List<QuestionLevel>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toQuestionList(value: String): List<QuestionLevel> {
        val gson = Gson()
        val type = object : TypeToken<List<QuestionLevel>>() {}.type
        return gson.fromJson(value, type)
    }
}