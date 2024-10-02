package com.example.socialmedia.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.common.State
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.notifation.testnofi.PushNotification
import com.example.socialmedia.ui.notifation.testnofi.RetrofitInstance
import com.example.socialmedia.ui.utils.Constants
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(val repository: Repository) : BaseViewModel(repository) {
    private var _listItemHome: MutableLiveData<ArrayList<ItemHome>> = MutableLiveData()
    val listItemHomeLiveData: LiveData<ArrayList<ItemHome>> get() = _listItemHome

    private val _userData = MutableLiveData<Profile>()
    val userData: LiveData<Profile> get() = _userData

    fun getUserData(uid: String) {
        repository.getUserByIdProfile(uid,Constants.KEY_COLLECTION_USER) {
            _userData.postValue(it)
        }
    }

    fun getDataHome() {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllHome() {
                isLoading.postValue(false)
                when (it) {
                    is State.Success -> {
                        if (it.data.size > 0) {
                            _listItemHome.postValue(it.data.sortedByDescending { it.datetime }
                                .toMutableList() as ArrayList<ItemHome>?)
                        }
                    }

                    else -> {

                    }
                }

            }
        }

        fun loadMoreHome() {
            viewModelScope.launch(Dispatchers.IO) {
                listItemHomeLiveData.value?.let {
                    repository.getAllHome(it[it.size - 1]) {
                        when (it) {
                            is State.Success -> _listItemHome.value?.addAll(it.data)

                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }

    fun getUser(userId: String?, callback: (State<Profile>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProfile(userId, callback)
        }
    }

    fun addListenerItemHomeChange(itemHome: ItemHome, callback: (ItemHome) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLike(itemHome) {
                when (it) {
                    is State.Success -> {
                        callback.invoke(it.data)
                    }

                    else -> {}
                }
            }
        }
    }

    fun updatePostLike(itemHome: ItemHome) {
        viewModelScope.launch(Dispatchers.IO) {
            val isLiked = itemHome.listUserLike?.contains(repository.getUid()) ?: false
            repository.updatePostLike(itemHome, isLiked) {
                if (!isLiked && itemHome.userid?.equals(repository.getUid()) == false) createNotification(
                    itemHome
                )
            }
        }
    }

    private fun createNotification(itemHome: ItemHome) {
            itemHome.userid?.let {
                repository.gettokenreceiver(it) { token ->
                    viewModelScope.launch(Dispatchers.IO) {
                        val notification = PushNotification(
                            repository.createNotification(
                                "Has like you",
                                "like",
                                it,
                                itemHome.key
                            ), token
                        )
                        sendNotification(notification)
                    }
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
    fun updateGold(gold: Int, callback: (State<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGold(gold, callback)
        }
    }

    fun getlistBlock(): MutableList<String> {
        var listreturn = mutableListOf<String>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getlistblock()
        }
        return listreturn
    }
}
