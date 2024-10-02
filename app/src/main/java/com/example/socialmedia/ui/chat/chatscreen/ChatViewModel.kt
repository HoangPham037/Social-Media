package com.example.socialmedia.ui.chat.chatscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.common.State
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.Conversion
import com.example.socialmedia.model.MuteNotification
import com.example.socialmedia.model.Profile
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.ui.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(val repository: Repository, val sharedPreferences: Preferences) : ViewModel() {
    private val _listConversion: MutableLiveData<List<Conversion>?> = MutableLiveData()
    val listConversion: MutableLiveData<List<Conversion>?> get() = _listConversion
    fun getAllChatroomCollectionReference() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllChatroomCollectionReference { state ->
                when (state) {
                    is State.Success -> _listConversion.postValue(state.data)
                    else -> {}
                }
            }
        }
    }

    fun getOtherUserFromConversion(
        listUid: List<String>,
        callback: (state: State<Profile>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOtherUserFromConversion(listUid, callback)
        }
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

    fun addToListUidNeedMute(muteNotification: MuteNotification) {
        val existingList = getListUidNeedMute()
        val isDuplicate = existingList.any { existingItem ->
            existingItem.uid == muteNotification.uid
        }
        if (!isDuplicate) {
            val newList = if (existingList.isEmpty()) {
                listOf(muteNotification)
            } else {
                existingList.plus(muteNotification)
            }
            val gson = Gson()
            val json = gson.toJson(newList)
            sharedPreferences.setString(Constants.KEY_LIST_UID_NEED_MUTE, json)
        }
    }

    fun removeUidInListNeedMute(muteNotification: MuteNotification) {
        val existingList = getListUidNeedMute().toMutableList()
        val itemToRemove = existingList.find { muteNotifications ->
            muteNotifications.uid == muteNotification.uid
        }
        if (itemToRemove != null) {
            existingList.remove(itemToRemove)
            val gson = Gson()
            val json = gson.toJson(existingList.toList())
            sharedPreferences.setString(Constants.KEY_LIST_UID_NEED_MUTE, json)
        }
    }

    private fun getListUidNeedMute(): List<MuteNotification> {
        val listUid: ArrayList<MuteNotification>
        val serializedObject = sharedPreferences.getString(Constants.KEY_LIST_UID_NEED_MUTE)
        if (serializedObject != null) {
            val gson = Gson()
            val type = object : TypeToken<List<MuteNotification?>?>() {}.type
            listUid = gson.fromJson(serializedObject, type)
            return listUid
        }
        return emptyList()
    }

    fun updateIsMute(isMute: Boolean, conversionId: String, keyUpdate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsMute(isMute, conversionId, keyUpdate)
        }
    }
}