package com.example.socialmedia.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.socialmedia.base.Adapter.RecyclerView.Itme
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.common.State
import com.example.socialmedia.di.BaseApplication
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.Comment
import com.example.socialmedia.model.Conversion
import com.example.socialmedia.model.ConversionSingleTon
import com.example.socialmedia.model.ImageModel
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.model.ItemHomeSingleton
import com.example.socialmedia.model.MessageModel
import com.example.socialmedia.model.Notification
import com.example.socialmedia.model.Profile
import com.example.socialmedia.model.ProfileSigleton
import com.example.socialmedia.model.ReplyComment
import com.example.socialmedia.ui.loginstuff.login.LoginCallBack
import com.example.socialmedia.ui.loginstuff.signup.SignUpCallBack
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOW
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWING
import com.facebook.AccessToken
import com.facebook.AccessTokenManager.Companion.TAG
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class Repository(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    val callback: CallbackManager
) : BaseRepository {


    fun FireBaseFireStore(data: HashMap<String, *>, collection: String, document: String) {
        firebaseFirestore.collection(collection).document(document).set(data).addOnSuccessListener {
            Log.d("777", "askjdnajwd: ")
        }.addOnFailureListener {
            Log.d("777", "fail push data: ${it.message}")
        }
    }

    fun updatedata(collection: String, documentId: String, data: HashMap<String, Any?>) {
        firebaseFirestore.collection(collection).document(documentId).update(data)
    }

    fun addpref(
        data: List<String>,
        collection: String,
        userId: String,
        callback: (State<Boolean>) -> Unit
    ) {
        val hien = firebaseFirestore.collection(collection).whereEqualTo("id", userId)
        hien.get().addOnSuccessListener {
            for (item in it.documents) {
                val document = item.reference
                document.update("pref", data)
                val success = State.Success(true)
                callback(success)
            }
        }.addOnFailureListener {

        }.addOnFailureListener {
            val fail = State.Add(false)
        }
    }

    fun getDataFromFireStore(name: String, callback: (List<Itme>) -> Unit) {
        firebaseFirestore.collection(name).get().addOnSuccessListener {
            val itemList = mutableListOf<Itme>()
            for (document in it) {
                val name1 = document.getString("name") ?: ""
                val item = Itme(name1)
                itemList.add(item)
            }
            callback(itemList)
        }
    }

    private val list = mutableListOf<Profile>()

    fun getUserFromFireStoreWithCollection(
        name: String,
        id: String,
        callback: (List<Profile>) -> Unit
    ) {
        list.clear()
        firebaseFirestore.collection(name).document(id).get().addOnSuccessListener {
            if (it != null) {
                val id1 = it["id"] as? String
                val listBlock = it["listBlock"] as? ArrayList<String>
                val address = it["address"] as? String
                val dob = it["dateOfBirth"] as? String
                val avt = it["avtPath"] as String
                val name1 = it["name"] as? String
                val email = it["email"] as String
                val phone = it["phone"] as? String
                val des = it["description"] as? String
                val listfollowing = it["listfollowing"] as? ArrayList<String>
                val listPost = it["listPost"] as? ArrayList<String>
                val listfollower = it["listfollower"] as? ArrayList<String>
                val isfollow = it["isFollow"] as Boolean
                val status = it["status"] as String
                val user = Profile(
                    id1,
                    listBlock,
                    address,
                    dob,
                    avt,
                    name1,
                    email,
                    phone,
                    "",
                    des,
                    listfollowing,
                    listPost,
                    listfollower,
                    isfollow,
                    status
                )
                list.add(user)
            }
            callback(list)
        }.addOnFailureListener {

        }
    }


    fun gethotsearch(name: String, id: String, callback: (List<ItemHome>) -> Unit) {
        var list = mutableListOf<ItemHome>()
        firebaseFirestore.collection(name).document(id).get().addOnSuccessListener {
            if (it != null && it.exists()) {
                val data = it.toObject(ItemHome::class.java)
                if (data != null) {
                    list.add(data)
                    callback(list)
                }
            }
        }
    }


    fun RegisterFirebasebyAccount(email: String, password: String, callback: SignUpCallBack) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback.onLoginSuccess()
            } else {
                callback.onLoginFailure(it.exception!!)
            }
        }
    }

    fun LoginFireBase(email: String, password: String, callback: LoginCallBack) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback.onLoginSuccess()
            } else {
                callback.onLoginFailure(it.exception!!)
            }
        }
    }

    fun getUid(): String? {
        val user = firebaseAuth.currentUser
        return user?.uid
    }

    fun LoginViaFacebook(
        activity: BaseFragmentWithBinding<*>,
        callback: (State<LoginResult>) -> Unit
    ) {
        LoginManager.getInstance()
            .registerCallback(this.callback, object : FacebookCallback<LoginResult> {
                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {
                }

                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken
                    Preferences.getInstance(BaseApplication.getInstance().applicationContext)
                        .setString(Constants.TOKEN_KEY, token.token.toString())
                    val credential = FacebookAuthProvider.getCredential(token.token)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val loginResult = State.Success(result)
                                callback(loginResult)
                            } else {
                                val exception = task.exception
                                Log.d("111", "onSuccess: $exception")
                            }
                        }
                }
            })
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email"))
    }

    fun getUserInfoFacebook(callback: (String?) -> Unit) {
        val accessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(accessToken) { _, response ->
            val userName = response?.jsonObject?.getString("name")
            Log.d("2222", "onSuccess: $userName")
            callback(userName)
        }
        val parameters = Bundle()
        parameters.putString("fields", "name")
        request.parameters = parameters
        request.executeAsync()
    }

    fun checkDocumentExists(
        collection: String,
        documentName: String? = null,
        callback: (State<Boolean>) -> Unit
    ) {
        val resolvedDocumentName = documentName ?: "1"
        Log.d("hehe", "CheckLogInBefore11: $resolvedDocumentName")
        val documentRef = firebaseFirestore.collection(collection).document(resolvedDocumentName)
        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    callback(State.Success(true))
                    Log.d("hehe", "Success")
                } else {
                    callback(State.Add(false))
                    Log.d("hehe", "Fail")
                }
            }
            .addOnFailureListener { exception ->
                //callback(State.Error(exception.message))
            }
    }

    fun SignInWithGoogle(activity: FragmentActivity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("640231531045-8qgrdqpddm9npl79uvcd329diptiutjb.apps.googleusercontent.com")
            .requestEmail().build()
        val googlesignin = GoogleSignIn.getClient(activity, gso)
        val signInIntent = googlesignin.signInIntent
        startActivityForResult(activity, signInIntent, 234, null)
    }

    fun FirebaseAuthwithGoogle(account: GoogleSignInAccount, callBack: (State<Unit>) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: true
                    if (isNewUser) {
                        callBack(State.Success(Unit))
                    } else {
                        callBack(State.Moved(Unit))

                    }
                } else {
                    callBack(State.Error("Login failed"))
                }
            }
    }

    fun ResetPassword(email: String, callback: (State<Unit>) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) callback(State.Success(Unit)) else callback(State.Error("Xui"))
        }
    }


    fun getNewNotification(data: MutableLiveData<List<Notification>>) {
        firebaseFirestore.collection("Notifications").addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val listdata = data.value?.toMutableList() ?: mutableListOf()
            for (change in snapshot?.documentChanges.orEmpty()) {
                val document = change.document
                val newdata = document.toObject(Notification::class.java)
                when (change.type) {
                    DocumentChange.Type.ADDED -> {
                        if (newdata.isnew) {
                            listdata.add(newdata)
                        }
                    }

                    DocumentChange.Type.MODIFIED -> {
                        /*   if (newdata.isnew){
                               listdata.add(newdata)
                           }*/
                        /*val existingIndex = listdata.indexOfFirst { it.key == newdata.key }
                        if (existingIndex != -1) {
                            listdata[existingIndex] = newdata
                        }*/
                    }

                    DocumentChange.Type.REMOVED -> {}
                }
            }
            data.value = listdata
        }
    }

    fun getNotification(data: MutableLiveData<List<Notification>>) {
        firebaseFirestore.collection("Notifications").addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            val listData = data.value?.toMutableList() ?: mutableListOf()

            for (change in snapshot?.documentChanges.orEmpty()) {
                val document = change.document
                val newData = document.toObject(Notification::class.java)
                when (change.type) {
                    DocumentChange.Type.ADDED -> {
                        if (newData.receiverUid == getUid()) {
                            listData.add(newData)
                        }
                    }

                    DocumentChange.Type.MODIFIED -> {
                        val existingIndex = listData.indexOfFirst { it.key == newData.key }
                        if (existingIndex != -1) {
                            listData[existingIndex] = newData
                        }
                    }

                    DocumentChange.Type.REMOVED -> {
                        val existingIndex = listData.indexOfFirst { it.key == newData.key }
                        if (existingIndex != -1) {
                            listData.removeAt(existingIndex)
                        }
                    }
                }
            }

            data.value = listData
        }
    }

    fun postNotification(callBack: (State<String>) -> Unit) {
        firebaseFirestore.collection("Notifications")
    }

    fun autoCompleteSearch2(
        collection: String,
        textwritein: String,
        autotext: String,
        callback: (List<String>, MutableList<String>?) -> Unit
    ) {
        val listname: MutableList<String> = mutableListOf()
        val listId: MutableList<String> = mutableListOf()
        val returnlist = mutableListOf<String>()
        val collectionRef: CollectionReference = firebaseFirestore.collection(collection)
        collectionRef.whereGreaterThanOrEqualTo(textwritein, autotext)
            .whereLessThanOrEqualTo(textwritein, autotext + "\uf8ff").get().addOnSuccessListener {
                it.documents.mapNotNull {
                    val listtext = it.getString(textwritein)
                    val textid = it.getString("id")
                    if (listtext != null) {
                        listname.add(listtext)
                        listId.add(textid!!)
                    }
                    for (item in listId) {
                        if (item !in getlistblock() && item != getUid()) {
                            returnlist.add(item)
                            Log.d("hihi", "autoCompleteSearch2: $returnlist")
                        }
                    }
                }
                callback(listname, returnlist)
            }
    }

    fun getlistblock(): MutableList<String> {
        var listreturn = mutableListOf<String>()
        listreturn.add(getUid()!!)
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(getUid()!!).get()
            .addOnSuccessListener {
                if (it != null) {
                    if (it.exists() && it.contains("listBlock")) {
                        val listblock: List<String>? = it.get("listBlock") as? List<String>
                        if (listblock != null) {
                            for (item in listblock) {
                                println()
                                listreturn.add(item)
                            }
                        } else {
                            Log.d("hienzd", "list null: ")
                        }
                    } else {
                        Log.d("hienzd", "field null: ")
                    }
                } else {
                    Log.d("hienzd", "doc null: ")
                }
            }
        return listreturn
    }

    fun getProfileUser(userId: String?, data: MutableLiveData<Profile>) {
        val id = getUid() ?: return
        if (userId != null) {
            firebaseFirestore.collection("Users").document(userId)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val newData = snapshot?.toObject(Profile::class.java)
                        newData?.let {
                            it.isFollow = it.listFollowers?.contains(id) == true
                            data.value = it
                        }
                    }
                }
        }
    }


    fun postComment(tvComment: String, post: ItemHome?, callBack: (Comment?) -> Unit) {
        val key = System.currentTimeMillis()
        val userId = getUid() ?: return
        val listLikeComment: ArrayList<String> = arrayListOf()
        val listReplyComment: HashMap<String, ReplyComment> = hashMapOf()
        val item =
            Comment(post?.key, userId, tvComment, listLikeComment, listReplyComment, null, key)
        post?.listCommnent?.set("$key", item)
        post?.let {
            post.key?.let { keyPost ->
                firebaseFirestore.collection("home").document(keyPost)
                    .update("listCommnent", post.listCommnent)
                    .addOnSuccessListener {
                        callBack(item)
                    }.addOnFailureListener {
                        callBack(null)
                    }
            }
        }
    }

    fun getProfile(userId: String?, callBack: (State<Profile>) -> Unit) {
        val id = userId ?: getUid() ?: return
        firebaseFirestore.collection("Users")
            .document(id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val item = documentSnapshot.toObject(Profile::class.java)
                    if (item != null) {
                        callBack(State.Success(item))
                    }
                }
            }
            .addOnFailureListener {
                callBack.invoke(State.Error(it.message.toString()))
                Log.d("hello", "getProfileUser: addOnFailureListener")
            }
    }

    fun addLike(itemHome: ItemHome, callback: (state: State<ItemHome>) -> Unit) {
        firebaseFirestore.collection(Constants.ITEM_HOME).document(itemHome.key.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val itemHomeChange = value.toObject(ItemHome::class.java)
                    if (itemHomeChange != null)
                        callback.invoke(State.Success(itemHomeChange))
                    else {
                        callback.invoke(State.Success(itemHome))
                    }
                } else {
                    callback.invoke(State.Success(itemHome))
                }

            }
    }

    fun getListFollowing(
        profile: Profile, lastVisibleDocument: Profile? = null,
        callback: (State<ArrayList<Profile>>) -> Unit
    ) {
        val userIds = profile.listFollowing as List<String>
        if (userIds.isEmpty()) callback(State.Success(arrayListOf()))
        firebaseFirestore.collection("Users")
            .apply { if (lastVisibleDocument != null) startAfter(lastVisibleDocument) }
            .whereIn("id", userIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val listProfile = ArrayList<Profile>()
                for (document in snapshot) {
                    val item = document.toObject(Profile::class.java)
                    if (item != null) {
                        item.isFollow = true
                        item.type = KEY_FOLLOWING
                        listProfile.add(item)
                    }
                }
                callback(State.Success(listProfile))
            }
            .addOnFailureListener { exception ->
                callback(State.Success(arrayListOf()))
            }
    }

    fun getListFollowers(
        profile: Profile, lastVisibleDocument: Profile? = null,
        callback: (State<ArrayList<Profile>>) -> Unit
    ) {
        val userIds = profile.listFollowers as List<String>
        firebaseFirestore.collection("Users")
            .apply { if (lastVisibleDocument != null) startAfter(lastVisibleDocument) }
            .whereIn("id", userIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val listProfile = ArrayList<Profile>()
                for (document in snapshot) {
                    val item = document.toObject(Profile::class.java)
                    if (item != null) {
                        item.isFollow = (profile.listFollowing?.contains(item.id) == true)
                        item.type = KEY_FOLLOW
                        listProfile.add(item)
                    }
                }
                callback(State.Success(listProfile))
            }
            .addOnFailureListener { exception ->
                callback(State.Success(arrayListOf()))
            }

    }

    fun getListFollowingUser(
        profile: Profile, lastVisibleDocument: Profile? = null,
        callback: (State<ArrayList<Profile>>) -> Unit
    ) {
        val userId = getUid() ?: callback(State.Success(arrayListOf()))
        val userIds = profile.listFollowing as List<String>
        if (userIds.isEmpty()) callback(State.Success(arrayListOf()))
        firebaseFirestore.collection("Users")
            .apply { if (lastVisibleDocument != null) startAfter(lastVisibleDocument) }
            .whereIn("id", userIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val listProfile = ArrayList<Profile>()
                for (document in snapshot) {
                    val item = document.toObject(Profile::class.java)
                    if (item != null) {
                        if (item.listFollowers?.contains(userId) == true) {
                            item.isFollow = true
                            item.type = KEY_FOLLOWING
                        } else {
                            item.isFollow = false
                            item.type = KEY_FOLLOW
                        }
                        listProfile.add(item)
                    }
                }
                callback(State.Success(listProfile))
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
                callback(State.Success(arrayListOf()))
            }
    }

    fun getListFollowersUser(
        profile: Profile, lastVisibleDocument: Profile? = null,
        callback: (State<ArrayList<Profile>>) -> Unit
    ) {
        val userId = getUid() ?: callback(State.Success(arrayListOf()))
        val userIds = profile.listFollowers as List<String>
        if (userIds.isEmpty()) callback(State.Success(arrayListOf()))

        firebaseFirestore.collection("Users")
            .apply { if (lastVisibleDocument != null) startAfter(lastVisibleDocument) }
            .whereIn("id", userIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val listProfile = ArrayList<Profile>()
                for (document in snapshot) {
                    val item = document.toObject(Profile::class.java)
                    if (item != null) {
                        if (item.listFollowers?.contains(userId) == true) {
                            item.isFollow = true
                            item.type = KEY_FOLLOWING
                        } else {
                            item.isFollow = false
                            item.type = KEY_FOLLOW
                        }
                        listProfile.add(item)
                    }
                }
                callback(State.Success(listProfile))
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
                callback(State.Success(arrayListOf()))
            }
    }


    fun getListBlock(
        profile: Profile, lastVisibleDocument: Profile? = null,
        callback: (State<ArrayList<Profile>>) -> Unit
    ) {

        val userIds = profile.listBlock as List<String>

        firebaseFirestore.collection("Users")
            .apply { if (lastVisibleDocument != null) startAfter(lastVisibleDocument) }
            .whereIn("id", userIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val listProfile = ArrayList<Profile>()
                for (document in snapshot) {
                    val item = document.toObject(Profile::class.java)
                    item.let {
                        item.isFollow = false
                        listProfile.add(item)
                    }
                }
                callback(State.Success(listProfile))
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
                callback(State.Success(arrayListOf()))
            }
    }

    fun removeUser(profile: Profile, callback: (Boolean) -> Unit) {
        if (profile.id != null && getUid() != null) {
            getUid()?.let { userId ->
                firebaseFirestore.collection("Users")
                    .document(userId)
                    .update("listFollowers", FieldValue.arrayRemove(profile.id))
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            profile.id?.let { id ->
                                firebaseFirestore.collection("Users")
                                    .document(id)
                                    .update("listFollowing", FieldValue.arrayRemove(userId))
                                    .addOnCompleteListener {
                                        callback(true)
                                    }
                            }
                        } else {
                            callback(false)
                        }
                    }
            }
        }
    }

    fun followers(profile: Profile) {
        if (profile.id != null && getUid() != null) {
            getUid()?.let { id ->
                firebaseFirestore.collection("Users").document(id)
                    .update(
                        "listFollowing",
                        if (!profile.isFollow) FieldValue.arrayUnion(profile.id) else FieldValue.arrayRemove(
                            profile.id
                        )
                    )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseFirestore.collection("Users").document(profile.id!!)
                                .update(
                                    "listFollowers",
                                    if (!profile.isFollow) FieldValue.arrayRemove(id) else FieldValue.arrayUnion(
                                        id
                                    )
                                )
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                    } else {
                                        val error = task.exception
                                    }
                                }
                        } else {
                            val error = task.exception
                        }
                    }
            }
        }
    }

    fun editAccount(type: String, textNew: String, callBack: (String) -> Unit) {
        val data = mutableMapOf<String, Any>()
        data[type] = textNew
        getUid()?.let {
            firebaseFirestore.collection("Users").document(it).update(data)
                .addOnSuccessListener {
                    callBack("Update successful")
                }
                .addOnFailureListener {
                    callBack("Update failed")
                }
        }
    }


    fun blockUser(profile: Profile) {
        getUid()?.let {
            firebaseFirestore.collection("Users")
                .document(it)
                .update(
                    "listBlock",
                    if (!profile.isFollow) FieldValue.arrayUnion(profile.id) else FieldValue.arrayRemove(
                        profile.id
                    )
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                    } else {
                        val error = task.exception
                    }
                }
        }
    }

    fun getAllImages(
        context: Context,
        callback: (state: State<ArrayList<ImageModel>>) -> Unit
    ) {
        callback.invoke(State.Loading)
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor?
        val column_index_data: Int
        val listOfAllImages = ArrayList<ImageModel>()
        var absolutePathOfImage: String? = null

        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        cursor = context.contentResolver!!.query(uri, projection, null, null, null)
        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor!!.moveToNext()) {
            absolutePathOfImage = cursor!!.getString(column_index_data)
            listOfAllImages.add(ImageModel(absolutePathOfImage))
        }
        callback.invoke(State.Success(listOfAllImages))
    }


    fun updatePostLike(
        post: ItemHome?,
        liked: Boolean,
        callback: (String) -> Unit
    ) {
        if (post == null) return
        post.key?.let {
            firebaseFirestore.collection("home").document(it)
                .update(
                    "listUserLike",
                    if (!liked) FieldValue.arrayUnion(getUid()) else FieldValue.arrayRemove(
                        getUid()
                    )
                )
                .addOnSuccessListener {
                    if (!liked) callback("add") else callback("remove")
                }.addOnFailureListener {
                    callback("fail")
                }
        }
    }


    fun getListDataPost(
        id: String?,
        lastVisibleDocument: ItemHome? = null,
        callback: (State<ArrayList<ItemHome>>) -> Unit
    ) {
        val userId: String = id ?: getUid() ?: return
        firebaseFirestore.collection(Constants.ITEM_HOME)
            .apply { if (lastVisibleDocument != null) startAfter(lastVisibleDocument) }
            .whereEqualTo("userid", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val itemList = ArrayList<ItemHome>()
                for (document in snapshot) {
                    val item = document.toObject(ItemHome::class.java)
                    item.let { itemList.add(it) }
                }
                callback(State.Success(itemList))
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
                callback(State.Success(arrayListOf()))
            }
    }


    fun upLoadImage(type: String, imageUri: Uri, callback: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef =
            storage.getReference().child("$type/${getUid()}")
        imageUri.let {
            storageRef.putFile(it).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                    getUid()?.let { avtPath ->
                        firebaseFirestore.collection("Users").document(avtPath)
                            .update("avtPath", uri.toString())
                    }
                }
            }.addOnFailureListener {
                callback("Fail")
            }
        }
    }

    fun updateGold(gold: Int, callback: (State<String>) -> Unit) {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USER)
            .document(getUid() ?: "")
            .update("gold", gold).addOnCompleteListener {
                callback.invoke(State.Success(""))
            }.addOnFailureListener {
                callback.invoke(State.Error(it.message.toString()))
            }
    }

    fun upLoadImageHome(imageUri: Uri?, callback: (State<String>) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        imageUri?.let {uri->
            uri.path?.let { path ->
                val fileName = path.substring(path.lastIndexOf('/') + 1)
                val storageRef = storage.getReference().child("itemHome/$fileName")
                storageRef.putFile(uri).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { url ->
                        callback(State.Success(url.toString()))
                    }
                }.addOnFailureListener {
                    callback(State.Error(it.message.toString()))
                }
            }
        }
    }


    fun getUserById(collection: String, callback: (Profile) -> Unit) {
        val userRef =
            getUid()?.let { firebaseFirestore.collection(collection).document(it) }
        userRef?.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null && snapshot.exists()) {
                val userData = snapshot.toObject(Profile::class.java)
                userData?.let { userInfo ->
                    callback(userInfo)
                }
            }
        }
    }

    fun getOtherUserById(
        uid: String,
        collection: String,
        callback: (Profile) -> Unit
    ) {
        val userRef = firebaseFirestore.collection(collection).document(uid)
        userRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null && snapshot.exists()) {
                val userData = snapshot.toObject(Profile::class.java)
                userData?.let { userInfo ->
                    callback(userInfo)
                }
            }
        }
    }

    fun updateStatus(status: String, uid: String, collection: String) {
        firebaseFirestore.collection(collection).document(uid)
            .update(Constants.KEY_STATUS, status)
    }

    fun updateStateSeen(state: Boolean, conversionId: String) {
        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
            .document(conversionId)
            .update(Constants.KEY_SEEN_STATE, state)
    }
    fun updateIsMute(isMute: Boolean, conversionId: String, keyUpdate:String) {
        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
            .document(conversionId)
            .update(keyUpdate, isMute)
    }

    fun addUserToBlock(uid: String) {
        getUid()?.let {
            firebaseFirestore.collection(Constants.KEY_COLLECTION_USER)
                .document(it)
                .update(
                    Constants.KEY_LIST_BLOCK,
                    FieldValue.arrayUnion(uid)
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                    } else {
                        val error = task.exception
                    }
                }
        }
    }

    fun removeUserToBlock(uid: String) {
        getUid()?.let {
            firebaseFirestore.collection(Constants.KEY_COLLECTION_USER)
                .document(it)
                .update(
                    Constants.KEY_LIST_BLOCK,
                    FieldValue.arrayRemove(uid)
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                    } else {
                        val error = task.exception
                    }
                }
        }
    }

    fun deleteConversionMessage(
        collectionId: String,
        callBack: (State<String>) -> Unit
    ) {
        callBack(State.Loading)
        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
            .document(collectionId)
            .delete()
            .addOnSuccessListener {
                callBack(State.Success("Success"))
            }
    }

    fun deleteConversion(
        collectionId: String,
        callBack: (State<String>) -> Unit
    ) {
        callBack(State.Loading)
        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
            .document(collectionId)
            .collection(Constants.KEY_COLLECT_CHAT_MESSAGE)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                }
                callBack(State.Success("Success"))
            }
    }


    fun uploadFileToFirebaseStorage(
        file: File,
        type: String,
        callback: (state: State<String>) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference.child("$type/${file.name}")
        val stream = FileInputStream(file)
        val uploadTask = storageRef.putStream(stream)
        callback.invoke(State.Loading)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnCompleteListener { taskResult ->
                val downloadUrl = taskResult.result.toString()
                callback.invoke(State.Success(downloadUrl))
            }
        }.addOnFailureListener {
            callback.invoke(State.Error("Can't send message"))
        }
    }

    fun uploadVideoToFirebaseStorage(uri: Uri, callback: (state: State<String>) -> Unit) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("video/video${UUID.randomUUID()}.mp4")
        val uploadTask = storageRef.putFile(uri)
        callback.invoke(State.Loading)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnCompleteListener { taskResult ->
                val downloadUrl = taskResult.result.toString()
                callback.invoke(State.Success(downloadUrl))
            }
        }.addOnFailureListener {
            callback.invoke(State.Error("Can't send this message."))
        }
    }

    fun uploadImageToFirebaseStorage(
        byteArray: ByteArray,
        callback: (state: State<String>) -> Unit
    ) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("image/image${UUID.randomUUID()}")
        val uploadTask = storageRef.putBytes(byteArray)
        callback.invoke(State.Loading)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnCompleteListener { taskResult ->
                val downloadUrl = taskResult.result.toString()
                callback.invoke(State.Success(downloadUrl))
            }
        }.addOnFailureListener {
            callback.invoke(State.Error("Can't send this message"))
        }
    }

    fun searchUserByName(
        nameSearch: String,
        field: String,
        collection: String,
        callback: (state: State<List<Profile>>) -> Unit
    ) {
        if (nameSearch.isEmpty()) {
            return callback(State.Change(emptyList()))
        }
        val regexPattern = Regex("(?i).*${Regex.escape(nameSearch)}.*")
        val listUser = ArrayList<Profile>()
        firebaseFirestore.collection(collection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val filteredDocument = querySnapshot.documents.filter {
                    it.getString(field)?.matches(regexPattern) ?: false
                }
                for (filterDocument in filteredDocument) {
                    val user = Profile()
                    user.id = filterDocument.getString(Constants.KEY_USER_ID)
                    user.name = filterDocument.getString(Constants.KEY_USER_NAME)
                    user.avtPath = filterDocument.getString(Constants.KEY_USER_AVATAR)
                    listUser.add(user)
                }
                listUser.sortByDescending { it.name }
                callback(State.Success(listUser))
            }
            .addOnFailureListener {
                callback(State.Success(emptyList()))
            }
    }

    fun addOrUpdateConversionReference(
        conversionId: String,
        senderId: String,
        receiverId: String,
        lastMessage: String,
        stateSeen: Boolean
    ) {
        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
            .document(conversionId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val conversion = task.result.toObject(Conversion::class.java)
                    if (conversion == null) {
                        val createConversion = Conversion(
                            id = conversionId,
                            listUid = listOf(senderId, receiverId),
                            lastMessageTimestamp = Timestamp.now(),
                            lastMessageSenderId = senderId,
                            lastMessage = lastMessage
                        )
                        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
                            .document(conversionId)
                            .set(createConversion)
                    } else {
                        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
                            .document(conversionId).update(
                                Constants.KEY_LAST_MESS_SENDER_ID,
                                senderId,
                                Constants.KEY_LAST_MESS_TIMESTAMP,
                                Timestamp.now(),
                                Constants.KEY_LAST_MESSAGE,
                                lastMessage,
                                Constants.KEY_SEEN_STATE,
                                stateSeen
                            )
                    }
                }
            }
    }

    fun addConversionMessageRef(
        conversionId: String,
        data: HashMap<String, *>,
        state: (state: State<String>) -> Unit
    ) {
        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
            .document(conversionId).collection(Constants.KEY_COLLECT_CHAT_MESSAGE)
            .add(data).addOnCompleteListener {
                state.invoke(State.Success("success"))
            }
            .addOnFailureListener {
                state.invoke(State.Error(it.printStackTrace().toString()))
            }
    }

    fun getConversionMessageRef(
        conversionId: String,
        onComplete: (state: State<ArrayList<MessageModel>>) -> Unit,
        lastVisibleDocumentCallback: (DocumentSnapshot) -> Unit
    ) {
        val listMessage = ArrayList<MessageModel>()
        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
            .document(conversionId)
            .collection(Constants.KEY_COLLECT_CHAT_MESSAGE)
            .orderBy(Constants.KEY_TIMESTAMP, Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onComplete(State.Success(arrayListOf()))
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (document in value.documentChanges) {
                        val messages = document.document.toObject(MessageModel::class.java)
                        listMessage.add(messages)
                    }
                    listMessage.sortWith(compareBy(MessageModel::timestamp))
                    val lastVisibleDocument = value.documents.lastOrNull()
                    onComplete(State.Success(listMessage))
                    if (lastVisibleDocument != null) {
                        lastVisibleDocumentCallback(lastVisibleDocument)
                    }
                }
            }
    }

    fun loadMoreConversionMessageRef(
        conversionId: String,
        lastVisibleDocument: DocumentSnapshot,
        onComplete: (state: State<ArrayList<MessageModel>>) -> Unit,
        lastVisibleDocumentCallback: (DocumentSnapshot) -> Unit
    ) {
        val listMessage = ArrayList<MessageModel>()
        firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
            .document(conversionId)
            .collection(Constants.KEY_COLLECT_CHAT_MESSAGE)
            .orderBy(Constants.KEY_TIMESTAMP, Query.Direction.DESCENDING)
            .startAfter(lastVisibleDocument)
            .limit(20)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onComplete(State.Success(arrayListOf()))
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (document in value.documentChanges) {
                        val messages = document.document.toObject(MessageModel::class.java)
                        listMessage.add(messages)
                    }
                    listMessage.sortWith(compareBy(MessageModel::timestamp))
                    val lastVisibleDocuments = value.documents.lastOrNull()
                    onComplete(State.Success(listMessage))
                    if (lastVisibleDocuments != null) {
                        lastVisibleDocumentCallback(lastVisibleDocuments)
                    }
                }
            }
    }

    fun getAllChatroomCollectionReference(
        callback: (state: State<List<Conversion>>) -> Unit
    ) {
        val listConversion = ArrayList<Conversion>()
        getUid()?.let {
            firebaseFirestore.collection(Constants.KEY_COLLECT_CHAT_ROOMS)
                .whereArrayContains(Constants.KEY_LIST_USER, it)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        callback(State.Success(emptyList()))
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        for (change in value.documentChanges) {
                            val document = change.document
                            val conversion = document.toObject(Conversion::class.java)
                            when (change.type) {
                                DocumentChange.Type.ADDED -> {
                                    listConversion.add(conversion)
                                }

                                DocumentChange.Type.MODIFIED -> {
                                    val existingIndex =
                                        listConversion.indexOfFirst { it.id == conversion.id }
                                    if (existingIndex != -1) {
                                        listConversion[existingIndex] = conversion
                                    }
                                }

                                DocumentChange.Type.REMOVED -> {
                                    val existingIndex =
                                        listConversion.indexOfFirst { it.id == conversion.id }
                                    if (existingIndex != -1) {
                                        listConversion.removeAt(existingIndex)
                                    }
                                }
                            }
                        }
                    }
                    listConversion.sortByDescending { it.lastMessageTimestamp }
                    callback(State.Success(listConversion))
                }
        }
    }

    fun getOtherUserFromConversion(
        listUid: List<String>,
        callback: (state: State<Profile>) -> Unit
    ) {
        if (listUid[0] == getUid()) {
            firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(listUid[1])
                .addSnapshotListener { value, error ->
                    if (error != null) return@addSnapshotListener
                    if (value != null && value.exists()) {
                        val userData = value.data
                        userData?.let {
                            val user = Profile()
                            user.id =
                                userData[Constants.KEY_USER_ID].toString()
                            user.name = userData[Constants.KEY_USER_NAME].toString()
                            user.avtPath = userData[Constants.KEY_USER_AVATAR].toString()
                            user.status = userData[Constants.KEY_STATUS].toString()
                            callback(State.Success(user))
                        }
                    }
                }
        } else {
            firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(listUid[0])
                .addSnapshotListener { value, error ->
                    if (error != null) return@addSnapshotListener
                    if (value != null && value.exists()) {
                        val userData = value.data
                        userData?.let {
                            val user = Profile()
                            user.id =
                                userData[Constants.KEY_USER_ID].toString()
                            user.name = userData[Constants.KEY_USER_NAME].toString()
                            user.avtPath = userData[Constants.KEY_USER_AVATAR].toString()
                            user.status = userData[Constants.KEY_STATUS].toString()
                            callback(State.Success(user))
                        }
                    }
                }
        }
    }

    fun getConversionId(userId1: String, userId2: String): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            userId1 + "_" + userId2
        } else {
            userId2 + "_" + userId1
        }
    }

    fun getAllHome(
        lastVisibleDocument: ItemHome? = null,
        callback: (State<ArrayList<ItemHome>>) -> Unit
    ) {
        firebaseFirestore.collection(Constants.ITEM_HOME)
            .apply { if (lastVisibleDocument != null) startAfter(lastVisibleDocument) }
            .get()
            .addOnSuccessListener { snapshot ->
                val itemList = ArrayList<ItemHome>()
                for (document in snapshot) {
                    val item = document.toObject(ItemHome::class.java)
                    item.let { itemList.add(it) }
                }
                ItemHomeSingleton.initialize(itemList)
                callback(State.Success(itemList))
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
                callback(State.Success(arrayListOf())) // Gọi callback với một danh sách trống nếu có lỗi
            }
    }


    fun addItemHome(itemHome: ItemHome, callback: (State<String>) -> Unit) {
        firebaseFirestore.collection(Constants.ITEM_HOME).document(itemHome.key.toString())
            .set(itemHome).addOnCompleteListener {
                callback.invoke(State.Success(""))
                getUid()?.let { it1 ->
                    firebaseFirestore.collection("Users").document(it1).update(
                        "listPost", FieldValue.arrayUnion(itemHome.key)
                    )
                }
            }.addOnFailureListener {
                callback.invoke(State.Error(it.message.toString()))
            }
    }

    fun deletePost(itemHome: ItemHome, callBack: (Boolean) -> Unit) {
        firebaseFirestore.collection("home").document(itemHome.key.toString())
            .delete().addOnSuccessListener {
                callBack(true)
                itemHome.userid?.let { it1 ->
                    firebaseFirestore.collection("Users").document(it1).update(
                        "listPost", FieldValue.arrayRemove(itemHome.key)
                    )
                }
            }.addOnFailureListener {
                callBack(false)
            }
    }

    fun getData(itemHome: ItemHome?, callBack: (ItemHome?) -> Unit) {
        itemHome?.let {
            it.key?.let { it1 ->
                firebaseFirestore.collection("home")
                    .document(it1)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) return@addSnapshotListener
                        val newData = snapshot?.toObject(ItemHome::class.java)
                        if (newData != null) callBack(newData) else callBack(null)
                    }
            }
        }
    }

    suspend fun createNotification(
        content: String,
        type: String,
        receiverId: String,
        idPost: String? = null
    ): Notification {
        return suspendCoroutine { continuation ->
            val key = System.currentTimeMillis().toString()
            val notification = Notification()
            var callbackInvoked = false

            getUserByIdProfile(getUid()!!, Constants.KEY_COLLECTION_USER) { user ->
                if (!callbackInvoked) {
                    notification.key = key
                    notification.avtpath = user.avtPath.toString()
                    notification.content = content
                    notification.type = type
                    notification.name = user.name.toString()
                    notification.isnew = true
                    notification.isread = false
                    notification.ispostnotification = false
                    notification.senderUid = getUid()!!
                    notification.receiverUid = receiverId
                    notification.idPost = idPost
                    if (type != "message") {
                        firebaseFirestore.collection("Notifications").document(key)
                            .set(notification)
                    }
                    continuation.resume(notification)
                    callbackInvoked = true
                }
            }
        }
    }



    fun getUserByIdProfile(uid: String, collection: String, callback: (Profile) -> Unit) {
        val userRef = firebaseFirestore.collection(collection).document(uid)
        userRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null && snapshot.exists()) {
                val userin = snapshot.toObject(Profile::class.java)
                if (userin != null) {
                    callback(userin)
                }
            }
        }

    }

    fun getUserByIdProfileLogin(uid: String, collection: String, callback: (Profile?) -> Unit) {
        val userRef = firebaseFirestore.collection(collection).document(uid)
        Log.d("zzzv", "getUserByIdProfile:")
        userRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null) {
                val userin = snapshot.toObject(Profile::class.java)
                Log.d("zzzv", "hehe: $userin ")
                userin.let {
                    callback(it)
                }
            }
        }
    }

    fun generateToken(): String {
        var token: String = ""
        val firebaseInstance = FirebaseInstallations.getInstance()
        firebaseInstance.id.addOnSuccessListener { installationid ->
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                token = it
                val hasHamp = hashMapOf<String, Any>("token" to token)
                getUid()?.let { it1 ->
                    firebaseFirestore.collection("Tokens").document(it1).set(hasHamp)
                        .addOnSuccessListener {
                        }
                }
            }
        }.addOnFailureListener {
        }
        return token
    }

    fun gettokenreceiver(receiverUid: String, callback: (String) -> Unit) {
        var token: String = ""
        firebaseFirestore.collection("Tokens").document(receiverUid).get().addOnSuccessListener {
            if (it != null) {
                token = it["token"] as String
                if (token != "") {
                    callback(token)
                }
            }
        }.addOnFailureListener {
            callback(token)
        }
    }

    fun checkifyouhavefollowuser(userId: String, callback: (Profile) -> Unit) {
        getUserByIdProfile(userId, Constants.KEY_COLLECTION_USER) {
            val followingList = it.listFollowing as? List<String>
            if (followingList != null && userId in followingList) {
                it.isFollow = true
                callback(it)
            } else {
                it.isFollow = false
                callback(it)
            }
        }
    }

    fun searchUserByNameFollow(
        index: Int,
        profile: Profile,
        nameSearch: String,
        field: String,
        collection: String,
        callback: (state: State<ArrayList<Profile>>) -> Unit
    ) {
        val listFollowing = profile.listFollowing?.toList()
        val listFollowers = profile.listFollowers?.toList()
        val userId = getUid() ?: return

        if (index == 0) {
            if (listFollowers?.isEmpty() == true || listFollowers?.size == 0) return
        } else {
            if (listFollowing?.isEmpty() == true || listFollowing?.size == 0) return
        }
        val regexPattern = Regex("(?i).*${Regex.escape(nameSearch)}.*")
        val listUser = ArrayList<Profile>()
        (if (index == 0) listFollowers else listFollowing)?.let {
            firebaseFirestore.collection(collection)
                .whereIn("id", it)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val filteredDocument =
                        querySnapshot.documents.filter { document ->
                            document.getString(field)?.matches(regexPattern)
                                ?: false
                        }
                    for (filterDocument in filteredDocument) {
                        val item = filterDocument.toObject(Profile::class.java)
                        if (item != null) {
                            if (item.listFollowers?.contains(userId) == true) {
                                item.isFollow = true
                                item.type = KEY_FOLLOWING
                            } else {
                                item.isFollow = false
                                item.type = KEY_FOLLOW
                            }
                            listUser.add(item)
                        }
                    }
                    listUser.sortByDescending { it.name }
                    callback(State.Success(listUser))
                }
        }
    }

    fun getPostWithKey(key: String, callback: (ItemHome) -> Unit) {
        firebaseFirestore.collection(Constants.ITEM_HOME).document(key).get()
            .addOnSuccessListener {
                val itemhome = it.toObject(ItemHome::class.java)
                if (itemhome != null) {
                    callback(itemhome)
                }
            }
    }

    fun getAllUser() {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).get().addOnSuccessListener {
            val itemlist = ArrayList<Profile>()
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val result = async {
                        for (doc in it) {
                            val item = doc.toObject(Profile::class.java)
                            item.let { itemlist.add(it) }
                        }
                    }
                    result.await()
                    ProfileSigleton.initialize(itemlist)
                } catch (e: Exception){
                    Log.d(TAG, "${e.message}: ")
                }
            }

        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting documents: ", exception)
        }
    }

    fun changePasswordFirebase(oldPassword: String, newPassword: String,onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        val user = FirebaseAuth.getInstance().currentUser

        if(user!=null){
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onFailure(updateTask.exception!!)
                        }
                    }
                } else {
                    onFailure(reauthTask.exception!!)
                }
            }
        }else {
            onFailure(Exception("User not signed in"))
        }
    }

    fun postImage(imageUri: Uri, callback: (String?) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef =
            storage.getReference().child("itemHome/${getUid()}")
        imageUri.let {
            storageRef.putFile(it).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                   callback(uri.toString())
                }
            }.addOnFailureListener {
                callback(null)
            }
        }
    }

    fun deleteComment(comment: Comment, callBack: (Boolean) -> Unit?) {

    }
}
