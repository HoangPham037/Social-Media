package com.example.socialmedia.model

import java.io.Serializable

data class PostNotification(var isPost: Boolean = false, var keyNotification: String = "") :
    Serializable