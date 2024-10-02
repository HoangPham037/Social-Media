package com.example.socialmedia.ui.loginstuff.forgotpassword

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentForgotPasswordBinding
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.loginstuff.CheckStuff
import org.koin.android.ext.android.inject

class ForgotPasswordFragment : BaseFragmentWithBinding<FragmentForgotPasswordBinding>(),CheckStuff {

    val viewmodel: MainViewModel by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentForgotPasswordBinding {
        return FragmentForgotPasswordBinding.inflate(layoutInflater)
    }

    override fun init() {
    }

    override fun initData() {
    }

    override fun initAction() {
        checkEmail(binding.edtEmail)
        binding.apply {
            btnResetPassword.click {
                if(isEmailValid(edtEmail.text.toString())) {
                    viewmodel.ResetpasswordFireBase(edtEmail.text.toString())
                }

            }
        }
        viewmodel.islogin.observe(viewLifecycleOwner, Observer {
            if(it) {
                toast("Success")
            } else {toast("Not Success")}
        })
        binding.btnBack.click{
            onBackPressed()
        }
    }


    override fun checkEmail(edt: EditText) {
        binding.apply {
            edtEmail.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    if(isEmailValid(edtEmail.text.toString())) {
                    } else {
                    }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(isEmailValid(edtEmail.text.toString())) {
                    } else {
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if(isEmailValid(edtEmail.text.toString())) {
                        edt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_greentick,0)
                        right.visibility = View.GONE
                    } else {
                        edt.setCompoundDrawablesWithIntrinsicBounds(0,0, 0,0)
                        right.visibility = View.VISIBLE
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