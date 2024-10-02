package com.example.socialmedia.ui.loginstuff.login

interface LoginCallBack {
    fun onLoginSuccess()
    fun onLoginFailure(error: Exception)
}