package com.example.socialmedia.ui.chat.chatscreen.chatwithuser

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.common.State
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.MessageModel
import com.example.socialmedia.model.MuteNotification
import com.example.socialmedia.model.Profile
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.ui.notifation.testnofi.PushNotification
import com.example.socialmedia.ui.notifation.testnofi.RetrofitInstance
import com.example.socialmedia.ui.utils.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ChatWithUserViewModel(val repository: Repository, val sharedPreferences: Preferences) : ViewModel() {

    private val _userInfo: MutableLiveData<Profile> = MutableLiveData()
    val userInfo: LiveData<Profile> get() = _userInfo
    fun getUserByUserId(collection: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserById(collection) {
                _userInfo.postValue(it)
            }
        }
    }

    private val _otherUserInfo: MutableLiveData<Profile> = MutableLiveData()
    val otherUserInfo: LiveData<Profile> get() = _otherUserInfo
    fun getOtherUserById(uid: String, collection: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOtherUserById(uid, collection) {
                _otherUserInfo.postValue(it)
            }
        }
    }

    fun updateStatus(status: String, uid: String, collection: String) {
        repository.updateStatus(status, uid, collection)
    }

    fun updateStateSeen(state: Boolean, conversionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStateSeen(state, conversionId)
        }
    }

    fun deleteConversion(
        collectionId: String,
        callback: (State<String>) -> Unit
    ) {
        repository.deleteConversion(collectionId, callback)
    }

    fun deleteConversionMessage(
        collectionId: String,
        callback: (State<String>) -> Unit
    ) {
        repository.deleteConversionMessage(collectionId, callback)
    }

    fun uploadFileToFirebaseStorage(
        file: File,
        type: String,
        callback: (state: State<String>) -> Unit
    ) {
        repository.uploadFileToFirebaseStorage(file, type, callback)
    }

    fun uploadVideoToFirebaseStorage(uri: Uri, callback: (state: State<String>) -> Unit) {
        repository.uploadVideoToFirebaseStorage(uri, callback)
    }

    fun uploadImageToFirebaseStorage(
        byteArray: ByteArray,
        callback: (state: State<String>) -> Unit
    ) {
        repository.uploadImageToFirebaseStorage(byteArray, callback)
    }

    fun getConversionId(userId1: String, userId2: String): String {
        return repository.getConversionId(userId1, userId2)
    }

    fun addOrUpdateConversionReference(
        conversionId: String,
        senderId: String,
        receiverId: String,
        lastMessage: String,
        stateSeen: Boolean
    ) {
        repository.addOrUpdateConversionReference(
            conversionId,
            senderId,
            receiverId,
            lastMessage,
            stateSeen
        )
    }

    private val _listMessage = MutableLiveData<ArrayList<MessageModel>>()
    val listMessage: MutableLiveData<ArrayList<MessageModel>> get() = _listMessage
    private val _lastDocumentSnapshot: MutableLiveData<DocumentSnapshot> = MutableLiveData()
    private val lastDocumentSnapshot: LiveData<DocumentSnapshot> get() = _lastDocumentSnapshot

    fun getConversionMessageRef(conversionId: String) {
        repository.getConversionMessageRef(conversionId, { state ->
            when (state) {
                is State.Success -> {
                    _listMessage.postValue(state.data!!)
                }

                else -> {}
            }
        }, {
            _lastDocumentSnapshot.postValue(it)
        })
    }

    fun loadMoreConversionMessageRef(conversionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            lastDocumentSnapshot.value?.let {
                Log.d("CheckScroll", "loadMoreConversionMessageRef: $it ")
                repository.loadMoreConversionMessageRef(conversionId, it, { state ->
                    when (state) {
                        is State.Success -> _listMessage.postValue(_listMessage.value?.plus(state.data) as ArrayList<MessageModel>)
                        else -> {}
                    }
                }, { lastDocumentSnapshots ->
                    _lastDocumentSnapshot.postValue(lastDocumentSnapshots)
                })
            }
        }
    }


    fun addConversionMessageRef(
        conversionId: String,
        data: HashMap<String, *>,
        state: (state: State<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Handler(Looper.getMainLooper()).postDelayed({
                repository.addConversionMessageRef(conversionId, data, state)
            }, 400)
        }

    }

    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {
            Log.e("ViewModelError", e.toString())
        }
    }

    fun createNotification(
        content: String,
        type: String,
        receiverId: String,
        callback: (PushNotification) -> Unit
    ) {
        repository.gettokenreceiver(receiverId) { token ->
            viewModelScope.launch(Dispatchers.IO) {
                val noti = repository.createNotification(content, type, receiverId)
                val notification = PushNotification(noti, token)
                callback(notification)
            }
        }
    }

    fun getUserid() : String?{
        return repository.getUid()
    }

    private val _listUidNeedMute : MutableLiveData<List<MuteNotification>> = MutableLiveData(
        emptyList()
    )
    val listUidNeedMute: LiveData<List<MuteNotification>> get() = _listUidNeedMute

    fun fetchListUidNeedMute() {
        viewModelScope.launch (Dispatchers.IO){
            _listUidNeedMute.postValue(getListUidNeedMute())
        }
    }

    private fun getListUidNeedMute() :List<MuteNotification> {
        val listUid : ArrayList<MuteNotification>
        val serializedObject = sharedPreferences.getString(Constants.KEY_LIST_UID_NEED_MUTE)
        if (serializedObject != null) {
            val gson = Gson()
            val type = object : TypeToken<List<MuteNotification?>?>() {}.type
            listUid = gson.fromJson(serializedObject, type)
            return listUid
        }
        return emptyList()
    }
}