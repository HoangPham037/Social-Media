package com.example.socialmedia.model

data class Comment(
    val key: String? = null,
    val userId: String? =null,
    var title: String? = null,
    var listLikeComment: ArrayList<String>? = arrayListOf(),
    var listReplyComment: HashMap<String, ReplyComment>? = hashMapOf(),
    var urlImg: String? = null,
    var datetime : Long? = null
)