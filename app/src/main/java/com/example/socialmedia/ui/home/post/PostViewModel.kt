package com.example.socialmedia.ui.home.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.common.SingleLiveEvent
import com.example.socialmedia.common.State
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel(val repository: Repository) : BaseViewModel(repository) {
    var itemHome: ItemHome? = null
    private val _isEnabledPost: SingleLiveEvent<Boolean?> = SingleLiveEvent()
    val proFile: MutableLiveData<Profile> = MutableLiveData()
    val isEnabledPost: LiveData<Boolean?> get() = _isEnabledPost
    fun checkData(s: String) {
        if (s.trim().isEmpty()) _isEnabledPost.call() else _isEnabledPost.postValue(true)
    }

    fun getUser(id : String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProfile(id) {
                when (it) {
                    is State.Success -> if (it.data != null) proFile.postValue(it.data)
                    else -> {

                    }
                }
            }

        }
    }
}