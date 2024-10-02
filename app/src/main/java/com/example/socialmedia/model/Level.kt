package com.example.socialmedia.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "Level")
data class Level(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: Int,

    @SerializedName("image")
    @ColumnInfo(name = "image")
    val image: String,

    @SerializedName("level")
    @ColumnInfo(name = "level")
    val level: String,

    @SerializedName("numberQuestion")
    @ColumnInfo(name = "numberQuestion")
    val numberQuestion: String,

    @SerializedName("numberQuestionDone")
    @ColumnInfo(name = "numberQuestionDone")
    var numberQuestionDone: String,

    @SerializedName("process")
    @ColumnInfo(name = "process")
    var process: String,

    @SerializedName("listQuestionLevel")
    @ColumnInfo(name = "listQuestionLevel")
    val listQuestionLevel: List<QuestionLevel>


) : Serializable
