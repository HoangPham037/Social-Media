package com.example.socialmedia.ui.home.comment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.common.State
import com.example.socialmedia.model.Comment
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.notifation.testnofi.PushNotification
import com.example.socialmedia.ui.notifation.testnofi.RetrofitInstance
import com.facebook.AccessTokenManager.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CommentViewmodel(val uRepository: Repository) : BaseViewModel(uRepository) {
    lateinit var itemHome: ItemHome
    var itemProfile: Profile? = null
    var liveDataItemHome: MutableLiveData<ItemHome?> = MutableLiveData()
    var liveDataProfile: MutableLiveData<Profile> = MutableLiveData()

    var profileLogin: MutableLiveData<Profile?> = MutableLiveData()
    fun getProfileUser(userid: String?, function: (State<Profile>) -> Unit) {
        uRepository.getProfile(userid, function)
    }

    fun getProfileLogin() {
        uRepository.getProfile(uRepository.getUid()) {
            when (it) {
                is State.Success -> {
                    profileLogin.postValue(it.data)
                }

                else -> {
                }
            }
        }
    }

    fun updatePostLike(callBack: (String) -> Unit) {
        val userId = uRepository.getUid()
        val isLiked = itemHome?.listUserLike?.contains(userId) ?: false
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.updatePostLike(itemHome, isLiked, callBack)
            if (!isLiked) createNotification(itemHome.userid, "Has like you", "like", itemHome.key)
        }
    }

    fun createNotification(userid: String?, content: String, type: String, key: String?) {
        userid?.let {
            uRepository.gettokenreceiver(it) { token ->
                viewModelScope.launch(Dispatchers.IO) {
                    val notification = PushNotification(
                        uRepository.createNotification(content, type, it, key),
                        token
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

    fun postComment(tvComment: String, callBack: (Comment?) -> Unit) {
        itemHome?.let { uRepository.postComment(tvComment, it, callBack) }
    }


    fun isLiked(): Boolean {
        val userId = uRepository.getUid()
        return itemHome?.listUserLike?.contains(userId) ?: false
    }

    fun getDataItemHome() {
        viewModelScope.launch(Dispatchers.IO) {
            itemHome?.let {
                uRepository.addLike(it) {
                    when (it) {
                        is State.Success -> {
                            itemHome = it.data
                            liveDataItemHome.postValue(it.data)
                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    fun deletePost(callBack: (Boolean) -> Unit) {
        itemHome?.let {
            viewModelScope.launch(Dispatchers.IO) {
                uRepository.deletePost(it, callBack)
            }
        }
    }

    fun deleteItemHome(data: ItemHome, callBack: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.deletePost(data, callBack)
        }
    }

    fun getData(itemHome: ItemHome?, callBack: (ItemHome?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getData(itemHome, callBack)
        }
    }

    fun getProfileUserLiveData(userid: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getProfileUser(userid, liveDataProfile)
        }
    }

    fun deleteComment(comment: Comment,  callBack: (State<String>) -> Unit) {
        liveDataItemHome.value?.listCommnent?.remove(comment.key)
        viewModelScope.launch(Dispatchers.IO) {
            liveDataItemHome.value?.let { uRepository.addItemHome(it, callBack) }
        }
    }
}