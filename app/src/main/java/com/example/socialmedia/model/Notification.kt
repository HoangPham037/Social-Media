package com.example.socialmedia.model

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Notification(
    var key: String = "",
    var name: String = "",
    var avtpath: String ="",
    var content: String = "",
    var receiverUid: String = "",
    var senderUid: String = "",
    var type: String = "",
    var isnew: Boolean = false,
    var isread : Boolean = false,
    var ispostnotification: Boolean = false,
    var idPost: String?=null
    //var token : String
):Serializable