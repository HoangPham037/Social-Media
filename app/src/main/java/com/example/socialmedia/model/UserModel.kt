package com.example.socialmedia.model

data class UserModel(
    var dateOfBirth: String? = "",
    var email: String? = "",
    var avtPath: String? = "",
    var name: String? = "",
    var id: String? = "",
    var description: String? = "",
    var key0: String? = "",
    var key1: String? = "",
    var key2: String? = "",
    var listFollowings: ArrayList<String>? = null,
    var listBlock: ArrayList<String>? = null,
    var status: String? = "",
    var listFavorite: ArrayList<String>? = null,
    var address: String? = null,
    var phone: String? = null,
    var type: String? = null,
    var listFollowers: ArrayList<String>? = null,
)

enum class ActiveStatus {
    Online,
    Offline,
    Typing
}
