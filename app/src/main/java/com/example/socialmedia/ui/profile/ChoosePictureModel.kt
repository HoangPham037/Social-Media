package com.example.socialmedia.ui.profile

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.common.State
import com.example.socialmedia.di.BaseApplication
import com.example.socialmedia.model.Comment
import com.example.socialmedia.model.ImageModel
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID


class ChoosePictureModel(  val repository: Repository) :
    BaseViewModel(repository) {
    private val listImageView : MutableLiveData<ArrayList<ImageModel>?> = MutableLiveData()
    val getListImageView: LiveData<ArrayList<ImageModel>?> get() =listImageView
    fun getAllImages() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllImages(BaseApplication.getInstance()){
                when(it){
                    is State.Loading -> isLoading.postValue(true)
                    is State.Success -> {
                        isLoading.postValue(false)
                        listImageView.postValue(it.data)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun saveAvatarImage(type : String ,uri: Uri, callback: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO){
            repository.upLoadImage(type ,uri , callback)
        }
    }

    fun postImage(imageUri: Uri, callback: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO){
            repository.postImage(imageUri, callback)
        }
    }

    fun postItemHome(uri: String,content : String? , callback: (State<String>) -> Unit) {
        val listUserLike : ArrayList<String> = arrayListOf()
        val listShare : ArrayList<String> = arrayListOf()
        val listCommnent: HashMap<String, Comment> = hashMapOf()
        val listUri : ArrayList<String> = arrayListOf(uri)
        val itemHome = ItemHome(UUID.randomUUID().toString(),repository.getUid(), content, listUri, listUserLike, listShare, listCommnent, System.currentTimeMillis())
        repository.addItemHome(itemHome ,callback )
    }

    fun getImageUri(bitmap: Bitmap, contentResolver: ContentResolver): Uri? {
        val bytes = ByteArrayOutputStream()
        var path: String? = null
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Uri.parse(path)
    }
}


