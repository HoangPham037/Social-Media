package com.example.socialmedia.ui.setting

import android.view.LayoutInflater

import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.databinding.FragmentLanguageBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.ui.utils.Constants.KEY_CHECKBOX
import org.koin.android.ext.android.inject

class LanguageFragment : BaseFragmentWithBinding<FragmentLanguageBinding>() {
    private var isCheckbox: String = "CheckboxEng"
    private val sharedPreferences: Preferences by inject()
    override fun init() {
        val tvCheck = sharedPreferences.getString(KEY_CHECKBOX)
        if (tvCheck == null || tvCheck == "CheckboxEng") {
            binding.checkboxEng.setBackgroundResource(R.drawable.ic_user_online)
        } else if (tvCheck == "CheckboxVni") {
            //binding.checkboxVni.setBackgroundResource(R.drawable.ic_user_online)
        }
    }

    override fun initData() {

    }

    override fun initAction() {
        /*binding.tbCheckboxVni.setOnClickListener {
            upDateUiCheckBox("CheckboxVni")
        }*/
        binding.tbCheckboxEng.setOnClickListener {
            upDateUiCheckBox("CheckboxEng")
        }
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    private fun upDateUiCheckBox(checkbox: String) {
        isCheckbox = checkbox
        binding.checkboxEng.setBackgroundResource(if (checkbox == "CheckboxEng" ) R.drawable.ic_user_online else R.drawable.ic_checkbox_unchecked)
       // binding.checkboxVni.setBackgroundResource(if (checkbox == "CheckboxVni" ) R.drawable.ic_user_online else R.drawable.ic_checkbox_unchecked)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.setString(KEY_CHECKBOX, isCheckbox)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentLanguageBinding {
        return FragmentLanguageBinding.inflate(layoutInflater)
    }
}


