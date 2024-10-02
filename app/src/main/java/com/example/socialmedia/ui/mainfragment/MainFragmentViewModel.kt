package com.example.socialmedia.ui.mainfragment

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.model.Profile
import com.example.socialmedia.repository.Repository

class MainFragmentViewModel(var repository: Repository) : BaseViewModel(repository) {
    private var profile : Profile? = null
    val livedataProfile: MutableLiveData<Profile> = MutableLiveData()

    fun getUid(): String? {
    return repository.getUid()
    }

    fun setProfile(profile: Profile) {
        this.profile = profile
    }
    fun upLoadImage(imageUri: Uri?, callback: (String) -> Unit) {
//        repository.upLoadImage(imageUri , callback)
    }
    fun getProfileUser(userId : String?) {
        repository.getProfileUser(userId, livedataProfile)
    }
}