package com.example.socialmedia.ui.profile.account

import android.util.Patterns
import android.view.LayoutInflater
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentChangeEmailBinding
import org.koin.android.ext.android.inject


class ChangeEmailFragment : BaseFragmentWithBinding<FragmentChangeEmailBinding>() {
    private val viewModel: AccountViewModel by inject()
    override fun init() {
        viewModel.getProfileUser(viewModel.repository.getUid())
    }


    override fun initData() {
        viewModel.livedataProfile.observe(viewLifecycleOwner) {
            binding.tvEmailOld.text = it.email
        }
    }


    override fun initAction() {
        binding.apply {
            btnSave.click { saveChangeUserName() }
            ivBack.click { onBackPressed() }
        }
    }

    private fun saveChangeUserName() {
        val textEmail = binding.edtTextEmail.text.toString().trim()
        if (textEmail.isEmpty()) {
            toast("Please enter your email")
        } else {
            if (isEmailValid(textEmail)) editEmail(textEmail) else toast("Please enter a valid email address.")
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun editEmail(textEmail: String) {
        viewModel.editAccount("email", textEmail) { sms ->
            toast(sms)
            if (sms == "Update successful") onBackPressed()
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentChangeEmailBinding {
        return FragmentChangeEmailBinding.inflate(layoutInflater)
    }
}


