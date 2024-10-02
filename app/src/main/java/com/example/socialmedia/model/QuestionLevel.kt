package com.example.socialmedia.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity
data class QuestionLevel(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("uid")
    var uid : Int,
    @SerializedName("question")
    val question : Int,

    @SerializedName("imageQuestion")
    val imageQuestion : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("isDone")
    var isDone : Boolean,

    @SerializedName("levelDetail")
    val levelDetail : String
) : Serializable
