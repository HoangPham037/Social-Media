package com.example.socialmedia.ui.profile.account

import android.content.ContentValues.TAG
import android.util.Log
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentAccountBinding
import com.example.socialmedia.ui.loginstuff.login.LogInScreenFragment
import com.example.socialmedia.ui.profile.CustomDialog
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject

class AccountFragment : BaseFragmentWithBinding<FragmentAccountBinding>(), View.OnClickListener {
    private val viewModel: AccountViewModel by inject()
    private var adapterAccount: AdapterAccount? = null

    override fun init() {
        viewModel.initDataSetting()
        viewModel.getProfileUser(viewModel.repository.getUid())
        adapterAccount = AdapterAccount {
            onClick(it)
        }
        viewModel.livedataProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                viewModel.initDataAccount(profile)
                setUpAdapter()
            }
        }
        if (viewModel.livedataProfile.value == null) {
            viewModel.initDataAccount(null)
            setUpAdapter()
        }
    }

    private fun setUpAdapter() {
        viewModel.listAccount.let { adapterAccount?.setData(it) }
        binding.rcvAccount.adapter = adapterAccount
    }

    override fun initData() {
        val adapterPersonal = AdapterAccount {
            onClick(it)
        }
        viewModel.listPersonal.let { adapterPersonal.setData(it) }
        binding.rcvPersonal.adapter = adapterPersonal
    }

    override fun initAction() {
        binding.apply {
            ivBack.click { onBackPressed() }
            tbDeleteAccount.click { deleteAccount() }
        }
    }

    private fun deleteAccount() {
        val dialog =    CustomDialog(requireContext(),"Delete Account" , "Are you sure you want to delete?", "No" , "Yes" ){
                if(it == "KEY_OK") checkTypeLogin()
            }
        dialog.show()
    }

    private fun checkTypeLogin() {
        FirebaseAuth.getInstance().currentUser?.providerData?.forEach {
            when (it.providerId) {
                "google.com" -> {
                    FirebaseAuth.getInstance().currentUser?.delete()?.addOnSuccessListener {
                        toast("Your account has been successfully deleted.")
                        openFragment(LogInScreenFragment::class.java, null, false)
                    }?.addOnFailureListener {
                        toast("The request failed. Please try again later.")
                    }
                }

                "firebase" -> {
                    FirebaseAuth.getInstance().currentUser?.delete()?.addOnSuccessListener {
                        toast("Your account has been successfully deleted.")
                        openFragment(LogInScreenFragment::class.java, null, false)
                    }?.addOnFailureListener {
                        toast("The request failed. Please try again later.")
                    }
                }

                "facebook.com" -> {
                    FirebaseAuth.getInstance().currentUser?.delete()?.addOnSuccessListener {
                        toast("Your account has been successfully deleted.")
                        openFragment(LogInScreenFragment::class.java, null, false)
                    }?.addOnFailureListener {
                        hideLoadingDialog()
                       // Log.d(TAG, "deleteAccount: " + it.message.toString())
                        toast("The request failed. Please try again later.")
                    }
                }
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentAccountBinding {
        return FragmentAccountBinding.inflate(layoutInflater)
    }

    override fun onClick(v: View) {
        val account = v.tag as Account
        when (account.tvName) {
            "Username" -> showFragmentChangeUserName()
            "Email" -> showFragmentChangeEmail()
            "Phone Number" -> toast("Tính năng đang được phát triển.")
            "Date of Birth" -> showFragmentChangeDate()
            "Country" -> toast("Tính năng đang được phát triển.")
            "Password" -> openFragment(ChangePasswordFrg::class.java,null,true)
            "Professional" -> toast("Tính năng đang được phát triển.")
            "Personal Data" -> toast("Tính năng đang được phát triển.")
            "Personal biography" -> openChangePersonal()
        }
    }

    private fun openChangePersonal() {
        openFragment(ChangePersonalFragment::class.java, null, true)
    }

    private fun showFragmentChangePassword() {
        toast("Tính năng đang được phát triển.")
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            val providerData = firebaseUser.providerData

            for (userInfo in providerData) {
                val providerId = userInfo.providerId

                when (providerId) {
                    "google.com" -> {
                        //Log.d(TAG, "CHANNNNNNNNNNNNNNNN google.com$providerData")
                    }

                    "facebook.com" -> {
                     //   Log.d(TAG, "CHANNNNNNNNNNNNNNNN facebook.com$providerData")
                    }

                    "password" -> {
                        //Log.d(TAG, "CHANNNNNNNNNNNNNNNN password$providerData")
                    }

                    else -> {
                       // Log.d(TAG, "CHANNNNNNNNNNNNNNNN khac$providerData")
                    }
                }
            }
        }
//        openFragment(ChangePasswordFrg::class.java, null, true)
    }

    private fun showFragmentChangeDate() {
        openFragment(ChangeDateOfBirthFrg::class.java, null, true)
    }

    private fun showFragmentChangeEmail() {
        openFragment(ChangeEmailFragment::class.java, null, true)
    }

    private fun showFragmentChangeUserName() {
        openFragment(ChangeUserNameFrg::class.java, null, true)
    }
}