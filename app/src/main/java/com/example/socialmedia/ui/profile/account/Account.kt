package com.example.socialmedia.ui.profile.account

import java.io.Serializable

data class Account(
    var id: Int? = 0,
    var tvName: String? = null,
    var tvContent: String? = null,
) : Serializable