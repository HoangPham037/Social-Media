package com.example.socialmedia.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.common.State
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.notifation.testnofi.PushNotification
import com.example.socialmedia.ui.notifation.testnofi.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileViewModel(val uRepository: Repository) :
    BaseViewModel(uRepository) {
    var userId: String? = null

    private var listPost: MutableLiveData<ArrayList<ItemHome>> = MutableLiveData()
    var liveDataProfile: MutableLiveData<Profile> = MutableLiveData()
    val listUser: MutableLiveData<ArrayList<Profile>> = MutableLiveData()
    val listPostUser: MutableLiveData<ArrayList<ItemHome>> = MutableLiveData()

    fun getProfileUser(userId: String?, callBack: (State<Profile>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getProfile(userId, callBack)
        }
    }

    fun blockUsers(profile: Profile?) {
        profile?.let {
            uRepository.blockUser(it)
        }
    }

    fun getListDataPost(id: String?) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getListDataPost(id) {
                isLoading.postValue(false)
                when (it) {
                    is State.Success -> {
                        if (it.data.size > 0) {
                            listPost.postValue(it.data.sortedByDescending { listPost -> listPost.datetime }
                                .toMutableList() as ArrayList<ItemHome>?)
                        }
                    }

                    else -> {

                    }
                }

            }
        }
    }

    fun getListDataPostUser(id: String?) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getListDataPost(id) {
                isLoading.postValue(false)
                when (it) {
                    is State.Success -> {
                        if (it.data.size > 0) {
                            listPostUser.postValue(it.data.sortedByDescending { listPostUser -> listPostUser.datetime }
                                .toMutableList() as ArrayList<ItemHome>?)
                        }
                    }

                    else -> {

                    }
                }
            }
        }
    }

    fun getProfile(it: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getProfileUser(it, liveDataProfile)
        }
    }

    fun follow(profile: Profile) {
        uRepository.followers(profile)
        if (!profile.isFollow) return
        profile.id?.let { createNotification(it) }
    }

    private fun createNotification(id: String) {
        uRepository.gettokenreceiver(id) { token ->
            viewModelScope.launch(Dispatchers.IO) {
                val notification = PushNotification(
                    uRepository.createNotification(
                        "Has follow you",
                        "follow",
                        id,
                        null
                    ), token
                )
                sendNotification(notification)
            }
        }
    }

    private fun sendNotification(notification: PushNotification) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {
                Log.e("ViewModelError", e.toString())
            }
        }
    }
}


