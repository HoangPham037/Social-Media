package com.example.socialmedia.ui.chat.chatscreen.chatwithuser.viewprofile

import androidx.lifecycle.ViewModel
import com.example.socialmedia.repository.Repository

class ViewProfileViewModel(private val repository: Repository) : ViewModel() {
    fun addUserToBlock(uid: String?) {
        if (uid == null) return
        repository.addUserToBlock(uid)
    }

    fun removeUserToBlock(uid: String?) {
        if (uid == null) return
        repository.removeUserToBlock(uid)
    }
}