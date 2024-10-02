package com.example.socialmedia.ui.firstTimeSetUp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.FragmentLogInFirstTimeBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.loginstuff.login.LogInScreenFragment
import com.example.socialmedia.ui.loginstuff.signup.SignUpFragment
import com.example.socialmedia.ui.mainfragment.MainFragment
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.ext.android.inject

class LogInFirstTimeFragment : BaseFragmentWithBinding<FragmentLogInFirstTimeBinding>() {
    var googlecode = 234
    val viewmodel : MainViewModel by inject()
    val callbackManager : CallbackManager by inject()
    val sharedPreferences: Preferences by inject()

    override fun getViewBinding(inflater: LayoutInflater): FragmentLogInFirstTimeBinding {
        return FragmentLogInFirstTimeBinding.inflate(layoutInflater)
    }

    override fun init() {
    }

    override fun initData() {
    }

    override fun initAction() {
        binding.apply {
            btnFbLoginLoginFirstTime.setOnClickListener{
                viewmodel.loginViaFacebook(this@LogInFirstTimeFragment)
                viewmodel.isLoading.observe(viewLifecycleOwner){
                    if(it) {
                        openFragment(MainFragment::class.java,null,false)
                        CheckLogInBefore()
                    } else {

                    }
                }


            }
            btnCreateAccountFirstTime.setOnClickListener {openFragment(SignUpFragment::class.java,null,true)}
            btnGoogleLoginFirstTime.click {
                SignInWithGoogle()
            }
            txtsigninfirsttime.click { openFragment(LogInScreenFragment::class.java,null,false) }
        }
    }

    fun CheckLogInBefore() {
        viewmodel.checkDocumentExists("Users",viewmodel.getUid()){
            when(it) {
                is State.Success -> {
                    openFragment(MainFragment::class.java,null,false)
                }
                else -> {
                    openFragment(SignUpFragment::class.java,null,false)
                    sharedPreferences.setBoolean("fblogin",true)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            googlecode -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    FirebaseAuthwithGoogle(account)
                } catch (e: ApiException) {
                    e.printStackTrace()
                    e.message?.let {
                        toast(it)
                    }
                }
            }

            else -> {
                callbackManager.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    fun SignInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.hehe)).requestEmail().build()
        val googlesignin = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googlesignin.signInIntent
        startActivityForResult(signInIntent, googlecode)
    }

    fun FirebaseAuthwithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: true
                    if (isNewUser) {
                        sharedPreferences.setBoolean("gglogin",true)
                        openFragment(SignUpFragment::class.java, null, false)
                    } else {
                        sharedPreferences.setBoolean("checkPref",true)
                        openFragment(MainFragment::class.java, null, false)
                    }
                } else {
                   toast("Login failed")
                }
            }
    }

}