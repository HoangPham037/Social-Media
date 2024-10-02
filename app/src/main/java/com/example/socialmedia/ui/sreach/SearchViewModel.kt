package com.example.socialmedia.ui.sreach

import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(val uRepository: Repository) : BaseViewModel(uRepository) {

    fun getWhoFollows(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO){
            uRepository.followers(profile)
        }
    }

    fun getAllUser(){
        viewModelScope.launch(Dispatchers.IO){
            uRepository.getAllUser()
        }
    }
}