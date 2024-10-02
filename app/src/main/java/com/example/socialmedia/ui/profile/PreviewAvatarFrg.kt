package com.example.socialmedia.ui.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.FragmentPreviewAvatarBinding
import com.example.socialmedia.ui.utils.Constants
import org.koin.android.ext.android.inject
import java.io.File


class PreviewAvatarFrg : BaseFragmentWithBinding<FragmentPreviewAvatarBinding>() {
    private var bitmap: Bitmap? = null
    private var uri: String? = null
    private var type: String? = null
    private var isPost: Boolean = false
    private val viewModel: ChoosePictureModel by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentPreviewAvatarBinding {
        return FragmentPreviewAvatarBinding.inflate(layoutInflater)
    }

    override fun init() {

        val bundle = arguments ?: return
        type = bundle.getString(Constants.KEY_TYPE)
        if (type?.equals(Constants.CAMERA) == true) {
            bitmap = if (Build.VERSION.SDK_INT >= 33) {
                bundle.getParcelable("bitmap_key" , Bitmap::class.java)
            }else{
                bundle.getParcelable("bitmap_key") as? Bitmap
            }

            Glide.with(this).load(bitmap).into(binding.ivAvatar)
        } else if (type?.equals(Constants.IMAGE) == true) {
            uri = bundle.getString(Constants.IMAGE) as String
            Glide.with(this).load(uri).into(binding.ivAvatar)
        }
    }

    override fun initData() {
        binding.ivAvatar.post {
            val width = binding.ivAvatar.width
            val params = binding.ivAvatar.layoutParams
            params.height = width
            binding.ivAvatar.layoutParams = params
        }
    }

    override fun initAction() {
        binding.apply {
            ivBack.click { onBackPressed() }
            tvSave.click { saveAvatar() }
            ivCheckPost.click { checkPost() }
        }
    }

    private fun checkPost() {
        isPost = !isPost
        binding.ivCheckPost.setImageResource(if (isPost) R.drawable.ic_radio_pressed else R.drawable.ic_radio_normal)
    }

    private fun saveAvatar() {
        if (type?.equals(Constants.CAMERA) == true) {
            bitmap?.let { bitmap ->
                showLoadingDialog()
                upDateAvt(viewModel.getImageUri(bitmap , requireContext().contentResolver))
            }
        } else if (type?.equals(Constants.IMAGE) == true) {
            uri?.let { uri ->
                showLoadingDialog()
                upDateAvt(Uri.fromFile(File(uri)))
            }
        }
    }

    private fun upDateAvt(imageUri: Uri?) {
        imageUri?.let {
            viewModel.saveAvatarImage("avatars",it) { sms ->
                if (sms == "Fail") {
                    toast("Unable to change avatar. Please try again later.")
                    hideLoadingDialog()
                } else {
                    if (isPost){
                        viewModel.postImage(imageUri){ uri ->
                            if (uri != null){
                                val content = binding.edtContent.text.toString().trim()
                                viewModel.postItemHome(uri , content){ sate ->
                                    hideLoadingDialog()
                                    onBackPressed()
                                    when(sate){
                                        is State.Success -> {
                                            toast("Avatar updated successfully!")
                                        }
                                        is State.Error ->{
                                            toast("Unable to change avatar. Please try again later.")
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }else{
                        toast("Avatar updated successfully!")
                        hideLoadingDialog()
                        onBackPressed()
                    }
                }
            }
        }
    }
}