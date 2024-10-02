package com.example.socialmedia.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.socialmedia.base.PermissionFragment
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentChoosePictureBinding
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.CAMERA
import com.example.socialmedia.ui.utils.Constants.IMAGE
import com.example.socialmedia.ui.utils.ImageUtils
import org.koin.android.ext.android.inject


@Suppress("DEPRECATION")
class ChoosePictureFrg : PermissionFragment<FragmentChoosePictureBinding>() {

    private var adapter: ChoosePictureAdapter? = null
    private val viewModel: ChoosePictureModel by inject()


    override fun getViewBinding(inflater: LayoutInflater): FragmentChoosePictureBinding {
        return FragmentChoosePictureBinding.inflate(inflater)
    }

    override fun init() {
        val bundle =Bundle()
        adapter = ChoosePictureAdapter {
            bundle.putString(IMAGE , it.uri)
            bundle.putString(Constants.KEY_TYPE , IMAGE)
            openFragment(PreviewAvatarFrg::class.java , bundle , true)
        }
        binding.rcvImageview.adapter = adapter
        openGallery {
            viewModel.getAllImages()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        viewModel.getListImageView.observe(viewLifecycleOwner) {
            val listImage = it?.toMutableList()?.reversed()
            if (listImage.isNullOrEmpty()) visibility(View.VISIBLE, View.GONE) else visibility(View.GONE, View.VISIBLE)
            adapter?.submitList(listImage)
        }
    }

    private fun visibility(visibility: Int, gone: Int) {
        binding.ivEmpty.visibility = visibility
        binding.tvEmpty.visibility = visibility
        binding.btnCamera.visibility = visibility
        binding.tbCamera.visibility = gone
    }

    override fun initAction() {
        binding.apply {
            ivBack.click { onBackPressed() }
            ivCamera.click { opCamera() }
            btnCamera.click { opCamera()  }
        }
    }

    private fun opCamera() {
        ImageUtils.takePhotoFromCamera(this)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_TAKE_PHOTO_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            data?.extras?.get("data")?.let {
            val bitmap =  it as Bitmap
                val bundle = Bundle()
                bundle.putParcelable("bitmap_key" , bitmap)
                bundle.putString(Constants.KEY_TYPE , CAMERA)
                openFragment(PreviewAvatarFrg::class.java , bundle , true)
            }
        }
    }
}