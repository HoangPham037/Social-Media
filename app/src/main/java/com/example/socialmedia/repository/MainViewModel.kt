package com.example.socialmedia.repository

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.Adapter.RecyclerView.Itme
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.common.SingleLiveEvent
import com.example.socialmedia.common.State
import com.example.socialmedia.model.ImageModel
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.loginstuff.login.LoginCallBack
import com.example.socialmedia.ui.loginstuff.signup.SignUpCallBack
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(val uRepository: Repository, val callBack: CallbackManager) :
    BaseViewModel(uRepository) {

    private val _listImage: MutableLiveData<ArrayList<ImageModel>> = MutableLiveData()
    val listImage: LiveData<ArrayList<ImageModel>> get() = _listImage
    fun setListImage(list: ArrayList<ImageModel>) {
        _listImage.postValue(list)
    }

    fun addDataFireStore(data: HashMap<String, *>, collection: String, document: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.FireBaseFireStore(data, collection, document)
        }
    }

    fun addPref(
        data: List<String>,
        collection: String,
        userId: String,
        callback: (State<Boolean>) -> Unit
    ) {
        uRepository.addpref(data, collection, userId, callback)
    }

    fun getDataFireStore(name: String, callback: (List<Itme>) -> Unit) {
        uRepository.getDataFromFireStore(name, callback)
    }

    fun getUserById(uid: String, callback: (Profile) -> Unit) {
        uRepository.getUserById(uid, callback)
    }

    fun RegisterFirebasebyAccount(email: String, password: String, callBack: SignUpCallBack) {
        uRepository.RegisterFirebasebyAccount(email, password, callBack)
    }

    fun LoginFireBaseAccount(email: String, password: String, callBack: LoginCallBack) {
        uRepository.LoginFireBase(email, password, callBack)
    }

    val _loginState = SingleLiveEvent<State<Boolean>>()
    val loginStateget = _loginState
    fun loginViaFacebook(activity: BaseFragmentWithBinding<*>) {
        uRepository.LoginViaFacebook(activity) {
            when (it) {
                is State.Success -> {
                    isLoading.postValue(true)
                    Log.d("loginViaFacebook", "loginViaFacebook: ")
                }

                else -> {
                    Log.d("loginViaFacebook", "loginViaFacebookzz: ")

                }
            }
        }
    }

    fun checkDocumentExists(
        collection: String,
        documentName: String? = null,
        callback: (State<Boolean>) -> Unit
    ) {
        uRepository.checkDocumentExists(collection, documentName, callback)
    }

    fun getUid(): String? {
        return uRepository.getUid()
    }

    val _userName = SingleLiveEvent<String?>()
    val userName = _userName

    fun fetchUserInfo() {
        uRepository.getUserInfoFacebook { userName ->
            _userName.value = userName
        }
    }

    fun SignInWithGoogle(activity: FragmentActivity) {
        uRepository.SignInWithGoogle(activity)
    }

    val _islogin = SingleLiveEvent<Boolean>()
    val islogin = _islogin
    fun FirebaseAuthwithGoogle(activity: GoogleSignInAccount) {
        uRepository.FirebaseAuthwithGoogle(activity) {
            when (it) {
                is State.Success -> {
                    _islogin.postValue(true)

                }
                else -> {
                    _islogin.postValue(false)
                }
            }
        }
    }

    fun ResetpasswordFireBase(email: String) {
        uRepository.ResetPassword(email) {
            when (it) {
                is State.Success -> {
                    _islogin.postValue(true)
                }

                else -> {
                    _islogin.postValue(false)
                }
            }
        }
    }

    fun removeImageSelect(imageModel: ImageModel) {
        _listImage.value?.remove(imageModel)


    }

    fun removeImageSelect(uri: String) {
        var item: ImageModel? = null
        _listImage.value?.forEach {
            if (it.uri == uri)
                item = it

        }
        _listImage.value?.remove(item)
    }

/*    fun receiverId(content: String, type: String, receiverId: String) {
        uRepository.createNotification(content, type, receiverId)
    }*/

    fun updateStatus(status: String, uid: String, collection: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uRepository.updateStatus(status, uid, collection)
        }
    }
    fun getUserByIdProfile(uid: String, collection: String, callback: (Profile?) -> Unit){
        uRepository.getUserByIdProfileLogin(uid,collection,callback)
    }

    fun changePasswrod(oldPassword: String, newPassword: String,onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        uRepository.changePasswordFirebase(oldPassword,newPassword,onSuccess,onFailure)
    }
}



