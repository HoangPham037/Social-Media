package com.example.socialmedia

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.socialmedia.base.BaseActivity
import com.example.socialmedia.base.utils.showToast
import com.example.socialmedia.databinding.ActivityMainBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.PostNotification
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.chat.chatscreen.ChatFragment
import com.example.socialmedia.ui.firstTimeSetUp.ChoosePreferencesFragment
import com.example.socialmedia.ui.firstTimeSetUp.LogInFirstTimeFragment
import com.example.socialmedia.ui.home.comment.CommentFragment
import com.example.socialmedia.ui.loginstuff.login.LogInScreenFragment
import com.example.socialmedia.ui.loginstuff.signup.SignUpFragment
import com.example.socialmedia.ui.mainfragment.MainFragment
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.sreach.SearchViewModel
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.KEY_COLLECTION_USER
import com.example.socialmedia.ui.utils.Constants.KEY_STATUS_OFFLINE
import com.example.socialmedia.ui.utils.Constants.KEY_STATUS_ONLINE
import com.facebook.FacebookSdk
import org.koin.android.ext.android.inject


class MainActivity() : BaseActivity<ActivityMainBinding>() {


    val sharedPreferences: Preferences by inject()
    val viewmodel: MainViewModel by inject()
    val viewmodelSearch: SearchViewModel by inject()

    private val RC_NOTIFICATION = 99

    override fun getViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun init() {
        val targetFragment = intent.getStringExtra("NavigateFrag")
        if(!targetFragment.isNullOrEmpty()){
            GoToFragment()
        } else {
            navigateNextScreen()
        }
        viewmodelSearch.getAllUser()
        checkPermissionAndroid13()
       // navigateNextScreen()
        FacebookSdk.sdkInitialize(applicationContext)
        binding.animationView.playAnimation()
    }


    private fun isFirstLaunch(): Boolean? {
        return sharedPreferences.getBoolean("firstLaunch")
    }

    private fun markFirstLaunch() {
        sharedPreferences.setBoolean("firstLaunch", true)
    }

    fun checkifusershavenameyet(callback : (Boolean)-> Unit) {
        val uid = viewmodel.getUid()
        if (uid != null) {
            viewmodel.getUserByIdProfile(uid, Constants.KEY_COLLECTION_USER) {
                if (it?.name != null && it.email != null && it.dateOfBirth != null) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }
    }

    private fun navigateNextScreen() {
        val uid = viewmodel.getUid()
        val checkPref = sharedPreferences.getBoolean("checkPref")
        val fragment = supportFragmentManager.findFragmentById(android.R.id.content)
        if (fragment == null) {
            Handler().postDelayed({
                if (isFirstLaunch() != true) {
                    openFragment(LogInFirstTimeFragment::class.java, null, false)
                    markFirstLaunch()
                } else {
                    if (uid != null && checkPref == true) {
                        if (isNetworkAvailable(this)) {
                            viewmodel.updateStatus(KEY_STATUS_ONLINE, uid, KEY_COLLECTION_USER)
                        } else {
                            viewmodel.updateStatus(KEY_STATUS_OFFLINE, uid, KEY_COLLECTION_USER)
                        }
                        openFragment(MainFragment::class.java, null, false)
                    } else {
                        if (uid != null) {
                            checkifusershavenameyet(){
                                if (!it) {
                                    sharedPreferences.setBoolean("gglogin", true)
                                    openFragment(SignUpFragment::class.java, null, false)
                                } else {
                                    openFragment(ChoosePreferencesFragment::class.java, null, false)
                                }
                            }
                        } else {
                            openFragment(LogInScreenFragment::class.java, null, false)
                        }
                    }
                }
            }, 2000)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        val uid = viewmodel.getUid()
        val checkPref = sharedPreferences.getBoolean("checkPref")
        if (isFirstLaunch() == true && uid != null && checkPref == true) {
            viewmodel.updateStatus(KEY_STATUS_OFFLINE, uid, KEY_COLLECTION_USER)
        }
    }

    fun checkPermissionAndroid13() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), RC_NOTIFICATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == RC_NOTIFICATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(isFirstPermit()!=true) {
                    showToast("ALLOWED")
                }
                markFirstPermit()
            } else {
                showToast("DENIED")
            }
        }
    }

    private fun isFirstPermit(): Boolean? {
        return sharedPreferences.getBoolean("firstpermit")
    }
    private fun markFirstPermit() {
        sharedPreferences.setBoolean("firstpermit", true)
    }

    fun GoToFragment(){
        val targetFragment = intent.getStringExtra("NavigateFrag")
        when (targetFragment) {
            "message" -> openFragment(ChatFragment::class.java,null,false)
            "follow" -> {
                val bundle = Bundle()
                val bundl1e = intent.getStringExtra(Constants.KEY_USERID)
                bundle.putString(Constants.KEY_USERID, bundl1e)
                openFragment(ProfileFragmentUsers::class.java,bundle,false)
            }
            "comment" -> {
                val bundle = Bundle()
                val idpost = intent.getStringExtra(Constants.KEY_ITEM_HOME)

                if(!idpost.isNullOrEmpty()){
                    viewmodel.uRepository.getPostWithKey(idpost){
                        bundle.putSerializable(Constants.KEY_ITEM_HOME,it)
                        openFragment(CommentFragment::class.java,bundle,false)
                    }
                }
                openFragment(PostNotification::class.java,bundle,false)
            }
            "like" -> {
                val bundle = Bundle()
                val idpost = intent.getStringExtra(Constants.KEY_ITEM_HOME)
                if(!idpost.isNullOrEmpty()){
                    viewmodel.uRepository.getPostWithKey(idpost){
                        bundle.putSerializable(Constants.KEY_ITEM_HOME,it)
                        openFragment(CommentFragment::class.java,bundle,false)
                    }
                }
            }
            else -> Intent(this, MainActivity::class.java)
        }
    }
}