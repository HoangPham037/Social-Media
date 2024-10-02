package com.example.socialmedia.model

import com.google.firebase.Timestamp

data class Conversion(
    var id: String?=null,
    var listUid: List<String>? =null,
    var lastMessageTimestamp: Timestamp ?= null,
    var lastMessageSenderId: String?= null,
    var lastMessage: String?=null,
    var isSeen:Boolean?=false,
    var isMuteSend:Boolean?=false,
    var isMuteReversion:Boolean?=false,
)

object ConversionSingleTon {
    private var itemHome: ArrayList<Conversion>? = null
    private var initializationCallback: ((ArrayList<Conversion>) -> Unit)? = null

    fun initialize(data: ArrayList<Conversion>, callback: ((ArrayList<Conversion>) -> Unit)? = null) {
        itemHome = data
        initializationCallback?.invoke(itemHome ?: ArrayList())
        initializationCallback = null
    }

    fun getInstance(callback: ((ArrayList<Conversion>) -> Unit)) {
        if (itemHome != null) {
            callback.invoke(itemHome ?: ArrayList())
        } else {
            initializationCallback = callback
        }
    }
}