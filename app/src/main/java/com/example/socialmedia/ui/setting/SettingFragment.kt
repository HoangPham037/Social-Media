package com.example.socialmedia.ui.setting


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentSettingBinding
import com.example.socialmedia.ui.loginstuff.login.LogInScreenFragment
import com.example.socialmedia.ui.mainfragment.MainFragmentViewModel
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.profile.CustomDialog
import com.example.socialmedia.ui.profile.account.AccountFragment
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject


class SettingFragment : BaseFragmentWithBinding<FragmentSettingBinding>() {
    private val viewModelMain: MainFragmentViewModel by inject()
    private var dialog: BottomSheetDialogFrg? = null
    private var bundle: Bundle? = null

    override fun init() {
        bundle = Bundle()
        dialog = BottomSheetDialogFrg {
            clickView(it)
        }
    }

    private fun clickView(view: View?) {
        if (view == null) return
        if (view.id == R.id.btn_add_acc) {
            addAccount()
        } else if (view.id == R.id.item_user) {
            toast("You are now logged in as ${(view.tag as Profile).name}")
        }
    }

    private fun addAccount() {
        toast("Tính năng đang được phát triển")
//        openFragment(SignUpFragment::class.java , null , true)
        dialog?.dismiss()
    }

    override fun initData() {
        viewModelMain.getProfileUser(viewModelMain.getUid())
        viewModelMain.livedataProfile.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it.avtPath).placeholder(R.drawable.avatar)
                .fallback(R.drawable.avatar).into(binding.ivAvt)
        }
    }

    override fun initAction() {
        binding.apply {
            ivBack.click { onBackPressed() }
            accDetail.click { showAccountFragment() }
            switchAccount.click { openBottomSheetDialog() }
            language.click { showLanguageFragment() }
            privacyPolicy.click { toast("Tính năng đang được phát triển") }
            contactSupport.click { toast("Tính năng đang được phát triển ") }
            logout.click { showDialog() }
        }
    }

    private fun showDialog() {
        val dialog = CustomDialog(requireContext(),"Confirm logout?" , "Do you want to logout?", "CANCEL" , "OK" ){
            if(it == "KEY_OK") {
                showLoadingDialog()
                showLogInScreenFragment()
                toast("Logout successful")
            }
        }
        dialog.show()
    }

    private fun showLogInScreenFragment() {
        onBackPressed()
        FirebaseAuth.getInstance().signOut()
        openFragment(LogInScreenFragment::class.java, null, false)
        hideLoadingDialog()
    }

    private fun showLanguageFragment() {
        openFragment(LanguageFragment::class.java, null, true)
    }

    private fun showAccountFragment() {
        openFragment(AccountFragment::class.java, null, true)
    }

    private fun openBottomSheetDialog() {
        if (viewModelMain.livedataProfile.value == null) return
        dialog?.setData(viewModelMain.livedataProfile.value as Profile)
        dialog?.show(childFragmentManager, SettingFragment::class.java.name)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(layoutInflater)
    }
}


