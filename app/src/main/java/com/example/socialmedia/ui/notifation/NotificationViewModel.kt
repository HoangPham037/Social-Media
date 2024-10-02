package com.example.socialmedia.ui.notifation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.model.Notification
import com.example.socialmedia.model.Profile
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.ui.notifation.testnofi.PushNotification
import com.example.socialmedia.ui.notifation.testnofi.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(val uRepository: Repository) : BaseViewModel(uRepository) {


    val _livedatatest: MutableLiveData<List<Notification>> = MutableLiveData()
    val livedatatest: LiveData<List<Notification>> get() = _livedatatest

    val _newnotifi: MutableLiveData<List<Notification>> = MutableLiveData()
    val newnotifi: LiveData<List<Notification>> get() = _newnotifi

    fun clearNewNotifications() {
        _newnotifi.value = emptyList()
    }

    fun checkpostdata() {
        uRepository.getNewNotification(_newnotifi)
    }

    fun checknewNofi() {
        viewModelScope.launch(Dispatchers.IO){
            uRepository.getNotification(_livedatatest)
        }
    }

    fun updatedata(data: HashMap<String, Any?>, collection: String, document: String) {
        uRepository.updatedata(document, collection, data)
    }

    fun getUid(): String? {
        return uRepository.getUid()
    }

    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {
            Log.e("ViewModelError", e.toString())
        }
    }

    fun gettoken(): String {
        return uRepository.generateToken()
    }


    fun getpostwithkey(key:String,callback: (ItemHome) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getPostWithKey(key,callback)
        }
    }

    fun getuserbyUid(uid: String, collection: String, callback: (Profile) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.getUserByIdProfile(uid,collection,callback)
        }
    }
}