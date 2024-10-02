package com.example.socialmedia.ui.chat.chatscreen.searchconversion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.common.State
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.Profile
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.ui.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchConversionViewModel(val repository: Repository, val sharedPreferences: Preferences) :
    ViewModel() {
    private val _listUser: MutableLiveData<State<List<Profile>?>> = MutableLiveData()
    val listUser: MutableLiveData<State<List<Profile>?>> get() = _listUser
    fun searchUserByName(
        nameSearch: String,
        field: String,
        collection: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchUserByName(nameSearch, field, collection) { state ->
                when (state) {
                    is State.Change -> _listUser.postValue(state)
                    is State.Success -> _listUser.postValue(state)
                    else -> {}
                }
            }
        }
    }

    private val _listHistoryProfile: MutableLiveData<List<Profile>> = MutableLiveData()
    val listHistoryProfile: LiveData<List<Profile>> get() = _listHistoryProfile

    fun addToSearchHistory(uid: String, profile: Profile) {
        val existingList = getListSearchHistory(uid)
        val isDuplicate = existingList.any { existingItem ->
            existingItem.id == profile.id
        }
        if (!isDuplicate) {
            val newList = if (existingList.isEmpty()) {
                listOf(profile)
            } else {
                existingList.plus(profile)
            }
            val gson = Gson()
            val json = gson.toJson(newList)
            sharedPreferences.setString(uid, json)
        }
    }

    fun fetchListSearchHistory(uid: String) {
        _listHistoryProfile.postValue(getListSearchHistory(uid))
    }

    private fun getListSearchHistory(uid: String): List<Profile> {
        val listSearch: ArrayList<Profile>
        val serializedObject = sharedPreferences.getString(uid)
        if (serializedObject != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Profile?>?>() {}.type
            listSearch = gson.fromJson(serializedObject, type)
            return listSearch
        }
        return emptyList()
    }

    fun deleteAllHistory() {
        sharedPreferences.removeString(Constants.KEY_LIST_HISTORY_SEARCH)
        _listHistoryProfile.postValue(emptyList())
    }
}