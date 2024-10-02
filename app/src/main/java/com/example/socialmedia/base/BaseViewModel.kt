package com.example.socialmedia.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialmedia.repository.Repository

abstract class BaseViewModel(private val repository: Repository): ViewModel() {
    val isLoading  : MutableLiveData<Boolean> = MutableLiveData(false)
}