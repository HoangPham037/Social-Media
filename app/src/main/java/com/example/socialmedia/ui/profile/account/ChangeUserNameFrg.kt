package com.example.socialmedia.ui.profile.account

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentChangeUserBinding
import org.koin.android.ext.android.inject


class ChangeUserNameFrg : BaseFragmentWithBinding<FragmentChangeUserBinding>() {
    private val viewModel: AccountViewModel by inject()
    override fun init() {
        viewModel.getProfileUser(
            viewModel.repository.getUid()
        )
    }


    override fun initData() {
        viewModel.livedataProfile.observe(viewLifecycleOwner) {
            binding.tvnameOld.text = it.name
        }
    }


    override fun initAction() {
        binding.apply {
            btnSave.click { saveChangeUserName() }
            ivBack.click { onBackPressed() }
        }
    }

    private fun saveChangeUserName() {
        val textName = binding.edtUserName.text.toString().trim()
        if (textName.isEmpty())   toast("Please enter your name")  else  editAccount(textName)
    }

    private fun editAccount(textName: String) {
        viewModel.editAccount("name", textName) { sms ->
            toast(sms)
            if (sms == "Update successful")  onBackPressed()
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentChangeUserBinding {
        return FragmentChangeUserBinding.inflate(layoutInflater)
    }
}


