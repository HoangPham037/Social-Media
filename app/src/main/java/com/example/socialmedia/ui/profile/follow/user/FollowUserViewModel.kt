package com.example.socialmedia.ui.profile.follow.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.common.State
import com.example.socialmedia.model.Profile
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.ui.notifation.testnofi.PushNotification
import com.example.socialmedia.ui.notifation.testnofi.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowUserViewModel(val uRepository: Repository) :
    BaseViewModel(uRepository) {
    var profile: Profile? = null
    var userId: String? = null
    var index: Int? = null
    var type: String? = null

    private var _listFollowing: MutableLiveData<ArrayList<Profile>> = MutableLiveData()
    private var _listFollowers: MutableLiveData<ArrayList<Profile>> = MutableLiveData()
    val listFollowingLiveData: LiveData<ArrayList<Profile>> get() = _listFollowing
    val listFollowersLiveData: LiveData<ArrayList<Profile>> get() = _listFollowers

    val listUser: MutableLiveData<ArrayList<Profile>> = MutableLiveData()
    fun getProfileUser(userId: String?, callBack: (State<Profile>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getProfile(userId, callBack)
        }
    }

    fun getListFollowing(profile: Profile) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getListFollowingUser(profile) {
                isLoading.postValue(false)
                when (it) {
                    is State.Success -> {
                        if (it.data.size > 0) {
                            _listFollowing.postValue(it.data.sortedByDescending { listFollowing -> listFollowing.name }
                                .toMutableList() as ArrayList<Profile>?)
                        }
                    }

                    else -> {

                    }
                }

            }
        }
    }

    fun getListFollowers(profile: Profile) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getListFollowersUser(profile) {
                isLoading.postValue(false)
                when (it) {
                    is State.Success -> {
                        if (it.data.size > 0) {
                            _listFollowers.postValue(it.data.sortedByDescending { listFollowers -> listFollowers.name }
                                .toMutableList() as ArrayList<Profile>?)
                        }
                    }

                    else -> {

                    }
                }

            }
        }
    }

    fun getProfile(id: String?, callBack: (State<Profile>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getProfile(id, callBack)
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

    fun searchUserByName(
        index: Int,
        profile: Profile,
        nameSearch: String,
        field: String,
        collection: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            uRepository.searchUserByNameFollow(
                index,
                profile,
                nameSearch,
                field,
                collection
            ) { state ->
                when (state) {
                    is State.Success -> listUser.postValue(state.data)
                    else -> {}
                }
            }
        }
    }
}


