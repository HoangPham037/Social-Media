package com.example.socialmedia.ui.profile.account

import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.fragment.app.FragmentManager
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.hideKeyboard
import com.example.socialmedia.databinding.FragmentChangePasswordBinding
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserFragment
import com.example.socialmedia.ui.mainfragment.MainFragment
import com.example.socialmedia.ui.setting.SettingFragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject


class ChangePasswordFrg : BaseFragmentWithBinding<FragmentChangePasswordBinding>() {

    private val viewmodel: MainViewModel by inject()
    override fun init() {

    }


    override fun initData() {

    }


    override fun initAction() {
        binding.apply {
            btnSave.click { saveChangePassword()
            view?.hideKeyboard(requireActivity())
            }
            ivBack.click { onBackPressed() }
        }
    }

    private fun saveChangePassword() {
        val passwordOld: String = binding.edtPassOld.getText().toString().trim()
        val passwordNew: String = binding.edtPassNew1.getText().toString().trim()
        val passwordVerify: String = binding.edtPassNew2.getText().toString().trim()
        if(passwordNew!=passwordOld) {
            if(passwordVerify==passwordNew) {
                viewmodel.changePasswrod(passwordOld,passwordNew, onSuccess = {
                     toast("Password changed successfully")
                    activity?.supportFragmentManager?.popBackStack(
                        SettingFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    //openFragment(MainFragment::class.java,null,false)
                }, onFailure = {
                    toast(it.message.toString())
                })
            } else {toast("Your new password not match")}
        } else {
            toast("Your new password must not match with your old password")
        }
        val user = FirebaseAuth.getInstance().currentUser
        val providerId = user?.providerId
        when (providerId) {
            "firebase" -> {
                Log.d(TAG, "CHANNNNNNNNNNNNNNNNNNNNNNNNN Đăng nhập bằng email và mật khẩu")
            }

            "google.com" -> {
                Log.d(TAG, "CHANNNNNNNNNNNNNNNNNNNNNNNNN Đăng nhập bằng Google Sign-In")
            }

            "facebook.com" -> {
                Log.d(TAG, "CHANNNNNNNNNNNNNNNNNNNNNNNNN Đăng nhập bằng Facebook Login")
            }
        }
    }


    override fun getViewBinding(inflater: LayoutInflater): FragmentChangePasswordBinding {
        return FragmentChangePasswordBinding.inflate(layoutInflater)
    }
}


