package com.example.socialmedia.ui.home.post.gallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.common.State
import com.example.socialmedia.model.ImageModel
import com.example.socialmedia.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryImageViewModel(val context: Context,val repository: Repository) : BaseViewModel(repository) {
    private val _imageAllGrallery : MutableLiveData<ArrayList<ImageModel>?> = MutableLiveData()
    val imageAllGrallery: LiveData<ArrayList<ImageModel>?> get() =_imageAllGrallery
    fun getAllImages(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllImages(context){
                when(it){
                   is State.Loading -> isLoading.postValue(true)
                   is State.Success -> {
                        isLoading.postValue(false)
                       _imageAllGrallery.postValue(it.data)

                    }
                    else -> {

                    }
                }
            }
        }
    }
}