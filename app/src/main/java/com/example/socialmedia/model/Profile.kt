package com.example.socialmedia.model

import java.io.Serializable

data class Profile(
    var id: String ?= "",
    var listBlock: ArrayList<String>? = arrayListOf(),
    var address: String? = "",
    var dateOfBirth: String? = "",
    var avtPath: String?="",
    var name: String? = "",
    var email: String? ="",
    var phone: String? = "",
    var type: String? ="",
    var description: String ?="",
    var listFollowing: ArrayList<String>? = arrayListOf(),
    var listPost: ArrayList<String>? = arrayListOf(),
    var listFollowers: ArrayList<String>? = arrayListOf(),
    var isFollow: Boolean = false,
    var status: String = "Offline",
    var gold: Int = 0
) : Serializable


object ProfileSigleton {
    private var itemHome: ArrayList<Profile>? = null
    private var initializationCallback: ((ArrayList<Profile>) -> Unit)? = null
    private var isInitialized = false

    fun initialize(data: ArrayList<Profile>) {
        itemHome = data
        isInitialized = true
        initializationCallback?.invoke(itemHome ?: ArrayList())
        initializationCallback = null
    }

    fun getInstance(callback: ((ArrayList<Profile>) -> Unit)) {
        if (isInitialized) {
            callback.invoke(itemHome ?: ArrayList())
        } else {
            initializationCallback = callback
        }
    }
}
