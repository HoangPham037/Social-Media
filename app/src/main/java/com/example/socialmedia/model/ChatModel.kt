package com.example.socialmedia.model

data class ChatModel(
    var id: String? = "",
    var isSeen: Boolean? = false,
    var uid: String? = "",
    var receiverId: String? = "",
    var type: String? = "",
    var message: String? = "",
    var duration: String? = "",
    var timestamp: String? = "",
    var conversionModel: ConversionModel? = null
)

data class ConversionModel(
    var id: String? = "",
    var conversionName: String? = "",
    var conversionImg: String? = "",
    var conversionActionState: String? = ""
)
