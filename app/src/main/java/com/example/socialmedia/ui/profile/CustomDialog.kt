package com.example.socialmedia.ui.profile

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.socialmedia.databinding.CustomDialogBinding

class CustomDialog(
    context: Context,
    val title: String,
    val content: String,
    private val tvCancel: String,
    private val tvOk: String,
    val callBack: (String) -> Unit
) : Dialog(context) {
    lateinit var binding: CustomDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomDialogBinding.inflate(layoutInflater)
        setCancelable(false)
        setContentView(binding.root)
        window?.setBackgroundDrawable(null)
        initViews()
    }

    private fun initViews() {
        binding.tvOk.text = tvOk
        binding.tvTitle.text = title
        binding.tvContent.text = content
        binding.tvCancel.text = tvCancel
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.tvOk.setOnClickListener {
            callBack("KEY_OK")
            dismiss()
        }
    }
}