package com.example.socialmedia.ui.loginstuff

import android.util.Patterns
import android.widget.EditText

interface CheckStuff {
    fun checkEmail(edt: EditText)

    fun isEmailValid(email: String): Boolean
}