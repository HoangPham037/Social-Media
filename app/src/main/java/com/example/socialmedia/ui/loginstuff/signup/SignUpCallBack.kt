package com.example.socialmedia.ui.loginstuff.signup

interface SignUpCallBack {
    fun onLoginSuccess()
    fun onLoginFailure(error: Exception)
}