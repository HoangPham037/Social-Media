package com.example.socialmedia.model

import com.google.firebase.Timestamp

data class MessageModel(
    var id: String?=null,
    var message: String? = null,
    var duration: String? = null,
    var senderId: String? = null,
    var timestamp: Timestamp?=null,
    var typeMessage: String?=null
)
