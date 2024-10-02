package com.example.socialmedia.ui.profile.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountViewModel(val repository: Repository) : BaseViewModel(repository) {
    val livedataProfile: MutableLiveData<Profile> = MutableLiveData()
    var listAccount: ArrayList<Account> = arrayListOf()
    var listPersonal: ArrayList<Account> = arrayListOf()
    fun getProfileUser(userId : String?) {
        repository.getProfileUser(userId, livedataProfile)
    }

    fun editAccount(type : String, textNew: String, function: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.editAccount(type ,textNew, function)
        }
    }

    fun initDataAccount(profile: Profile?) {
        listAccount.clear()
        listAccount.add(Account(R.drawable.ic_user, "Username", profile?.name  ))
        listAccount.add(Account(R.drawable.ic_email, "Email", profile?.email))
        listAccount.add(Account(R.drawable.ic_phone, "Phone Number", profile?.phone))
        listAccount.add(Account(R.drawable.ic_date, "Date of Birth", profile?.dateOfBirth))
        listAccount.add(Account(R.drawable.ic_country, "Country", "Việt Nam"))
        listAccount.add(Account(R.drawable.ic_edit_2, "Personal biography", profile?.description))
    }

    fun initDataSetting() {
        listPersonal.add(Account(R.drawable.ic_password, "Password", "Change your password"))
        listPersonal.add(Account(R.drawable.ic_professional, "Professional", "Switch to professional account"))
        listPersonal.add(Account(R.drawable.ic_data, "Personal Data", "Download your personal data"))
    }

    fun saveDescription(type: String, description: String, function: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.editAccount(type ,description, function)
        }
    }
}