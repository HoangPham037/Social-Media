package com.example.socialmedia.ui.loginstuff.login

import android.content.Intent
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.hideKeyboard
import com.example.socialmedia.databinding.FragmentLogInScreenBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.loginstuff.CheckStuff
import com.example.socialmedia.ui.loginstuff.forgotpassword.ForgotPasswordFragment
import com.example.socialmedia.ui.loginstuff.signup.SignUpFragment
import com.example.socialmedia.ui.mainfragment.MainFragment
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.delay_2000
import com.example.socialmedia.ui.utils.Constants.googlecode
import com.facebook.AccessTokenManager.Companion.TAG
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.android.inject

class LogInScreenFragment : BaseFragmentWithBinding<FragmentLogInScreenBinding>(), CheckStuff {
    private val viewModel: MainViewModel by inject()
    private val callBackManager: CallbackManager by inject()
    val sharedPreferences: Preferences by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentLogInScreenBinding {
        return FragmentLogInScreenBinding.inflate(layoutInflater)
    }

    override fun init() {
    }

    override fun initData() {
    }

    override fun initAction() {
        binding.apply {
            txtsigninfirsttime.click { openFragment(SignUpFragment::class.java, null, true) }
            forgotPW.click { openFragment(ForgotPasswordFragment::class.java, null, true) }
            checkEmail(edtEmail)
            checkNullEditTextPW((edtPassword))
            btnLogin.click {
                login()
                view?.hideKeyboard(requireActivity())
            }
            btnGoogleLogin.click {
                SignInWithGoogle()
                view?.hideKeyboard(requireActivity())
            }
            btnFbLogin.click {
                view?.hideKeyboard(requireActivity())
                viewModel.loginViaFacebook(this@LogInScreenFragment)
                viewModel.isLoading.observe(viewLifecycleOwner) {
                    if (it) {
                        openFragment(MainFragment::class.java, null, false)
                    } else {

                    }
                }
            }
        }
    }


    fun checkNullEditTextPW(edt: EditText) {
        var timer: CountDownTimer? = null
        var checkRevealPw : Boolean =false

        edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(delay_2000, delay_2000) {
                    override fun onTick(millisUntilFinished: Long) {
                        edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_x, 0)
                        edt.click { edt.setText("") }
                    }

                    override fun onFinish() {
                        updateIconVisibility(edt)
                        edt.click {
                            checkRevealPw = !checkRevealPw
                            togglePasswordVisibility(edt)
                        }
                    }
                }.start()
            }

        })
    }

    fun togglePasswordVisibility(editText: EditText) {
        val currentTransformationMethod = editText.transformationMethod
        if (currentTransformationMethod == PasswordTransformationMethod.getInstance()) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        editText.setSelection(editText.text.length)
    }

    fun updateIconVisibility(edt: EditText) {
        if (edt.text.isNullOrEmpty()) {
            edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            edt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_line, 0, 0, 0)
        } else {
            edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_hideeyes, 0)
        }
    }

    private fun login() {
        binding.apply {
            if (!edtEmail.text.isNullOrEmpty() && !edtPassword.text.isNullOrEmpty()) {
                viewModel.LoginFireBaseAccount(
                    edtEmail.text.toString(),
                    edtPassword.text.toString(),
                    object :
                        LoginCallBack {
                        override fun onLoginSuccess() {
                           openFragment(MainFragment::class.java, null, false)
                            sharedPreferences.setBoolean("checkPref", true)
                            view?.hideKeyboard(requireActivity())
                        }

                        override fun onLoginFailure(error: Exception) {
                            wrong.visibility = View.VISIBLE
                        }
                    })
            } else {
                wrong.visibility = View.VISIBLE
            }
        }
    }

    fun SignInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        val googlesignin = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googlesignin.signInIntent

        startActivityForResult(signInIntent, googlecode)

    }

    private fun FirebaseAuthwithGoogle(account: GoogleSignInAccount) {
        sharedPreferences.setString(Constants.TOKEN_KEY, account.idToken.toString())
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: true
                    if (isNewUser) {
                        sharedPreferences.setBoolean("gglogin", true)
                        openFragment(SignUpFragment::class.java, null, false)
                    } else {
                        sharedPreferences.setBoolean("checkPref", true)
                        openFragment(MainFragment::class.java, null, false)
                    }
                } else {
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
                callBackManager.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun checkEmail(edt: EditText) {
        binding.apply {
            edt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    updateIconVisibility(edt)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateIconVisibility(edt)
                }

                override fun afterTextChanged(s: Editable?) {
                    updateIconVisibility(edt)
                    if (isEmailValid(edt.text.toString())) {
                        edt.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_greentick,
                            0
                        )
                        binding.right.visibility = View.GONE
                    } else {
                        if (edt.text.toString().isNullOrEmpty()) {
                            binding.right.visibility = View.GONE
                        } else {
                            edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                            binding.right.visibility = View.VISIBLE
                        }
                    }
                }

            })
        }
    }

    override fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}

class PasswordVisibleTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return source
    }
}