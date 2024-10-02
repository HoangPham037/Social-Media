package com.example.socialmedia.ui.profile.follow

import android.net.Uri
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayoutStates.TAG
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FollowViewModel(val uRepository: Repository) :
    BaseViewModel(uRepository) {
    var profile: Profile? = null
    var isFollow: Boolean? = null
    var index: Int? = null
    var type: String? = null
    var userId: String? = null
    val listBlock: MutableLiveData<ArrayList<Profile>> = MutableLiveData()
    private var listPost: MutableLiveData<ArrayList<ItemHome>> = MutableLiveData()
    var liveDataProfile: MutableLiveData<Profile> = MutableLiveData()
    val listUser: MutableLiveData<ArrayList<Profile>> = MutableLiveData()

    private var _listFollowing: MutableLiveData<ArrayList<Profile>> = MutableLiveData()
    private var _listFollowers: MutableLiveData<ArrayList<Profile>> = MutableLiveData()
    val listFollowingLiveData: LiveData<ArrayList<Profile>> get() = _listFollowing
    val listFollowersLiveData: LiveData<ArrayList<Profile>> get() = _listFollowers
    val getListPost: LiveData<ArrayList<ItemHome>> get() = listPost
    fun getListBlock(profile: Profile) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getListBlock(profile) {
                isLoading.postValue(false)
                when (it) {
                    is State.Success -> {
                        if (it.data.size > 0) {
                            listBlock.postValue(it.data.sortedByDescending { listFollowers -> listFollowers.name }
                                .toMutableList() as ArrayList<Profile>?)
                        }
                    }

                    else -> {

                    }
                }
            }
        }
    }

    fun removeUser(profile: Profile, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.removeUser(profile, callback)
        }
    }

    fun getProfileUser(userId: String?, callBack: (State<Profile>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            uRepository.getProfile(userId, callBack)
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

    fun upLoadImage(imageUri: Uri?, callback: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
//            uRepository.upLoadImage(imageUri, callback)
        }
    }

    fun blockUsers(profile: Profile?) {
        if (profile == null) return
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.blockUser(profile)
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

    fun deletePost(data: ItemHome, callBack: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.deletePost(data, callBack)
        }
    }


    fun getProfile(it: String?) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getProfileUser(it, liveDataProfile)
        }
    }

    fun updatePostLike(itemHome: ItemHome) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = uRepository.getUid()
            val isLiked = itemHome.listUserLike.contains(id)
            uRepository.updatePostLike(itemHome, isLiked) {
                if (!isLiked && uRepository.getUid()
                        ?.equals(itemHome.userid) == false
                ) itemHome.userid?.let { it1 ->
                    createNotification(it1)
                }
            }
        }
    }

    fun addListenerItemHomeChange(itemHome: ItemHome, callback: (ItemHome) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.addLike(itemHome) {
                when (it) {
                    is State.Success -> {
                        callback.invoke(it.data)
                    }

                    else -> {}
                }
            }
        }
    }

    fun remove(profile: Profile) {
        listFollowersLiveData.value?.remove(profile)
    }

    fun getListFollowing(profile: Profile) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getListFollowing(profile) {
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
            uRepository.getListFollowers(profile) {
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
}


