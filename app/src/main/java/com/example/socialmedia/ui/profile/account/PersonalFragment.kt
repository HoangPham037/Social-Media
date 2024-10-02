package com.example.socialmedia.ui.profile.account

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentPersonalBinding
import org.koin.android.ext.android.inject


class PersonalFragment : BaseFragmentWithBinding<FragmentPersonalBinding>() {
    private val viewModel: AccountViewModel by inject()
    override fun init() {
        viewModel.getProfileUser(viewModel.repository.getUid())
    }


    override fun initData() {

    }


    override fun initAction() {
        binding.apply {
            btnSave.click { saveDescription() }
            ivBack.click { onBackPressed() }
            tvCancel.click { cancelSaveDescription() }
            lnDescription.click { openKeyboard() }
        }
    }

    private fun openKeyboard() {
        binding.edtDescription.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtDescription, 0)
    }

    private fun cancelSaveDescription() {
        val textDescription = binding.edtDescription.text.toString().trim()
        if (textDescription.isNotEmpty()) {
            openDialog()
        } else {
            onBackPressed()
        }
    }

    private fun openDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Exit Without Saving")
        builder.setMessage("Are you sure you want to exit without saving your changes?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            onBackPressed()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveDescription() {
        val textDescription = binding.edtDescription.text.toString().trim()
        if (textDescription.isEmpty())  toast("Please enter a description.") else  save(textDescription)
    }

    private fun save(description: String) {
        viewModel.saveDescription("description", description) { sms ->
            toast(sms)
            if (sms == "Update successful")   onBackPressed()
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentPersonalBinding {
        return FragmentPersonalBinding.inflate(layoutInflater)
    }
}


