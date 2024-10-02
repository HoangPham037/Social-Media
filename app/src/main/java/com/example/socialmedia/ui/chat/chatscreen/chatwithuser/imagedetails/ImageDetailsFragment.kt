package com.example.socialmedia.ui.chat.chatscreen.chatwithuser.imagedetails

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.showToasts
import com.example.socialmedia.databinding.FragmentImageDetailsBinding
import com.example.socialmedia.ui.utils.CustomToast
import com.ortiz.touchview.TouchImageView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageDetailsFragment : BaseFragmentWithBinding<FragmentImageDetailsBinding>() {
    override fun getViewBinding(inflater: LayoutInflater): FragmentImageDetailsBinding {
        return FragmentImageDetailsBinding.inflate(layoutInflater)
    }

    override fun init() {
    }

    override fun initData() {
        val uri = arguments?.getString("imgUrl")
        Glide.with(requireActivity()).load(uri).error(R.drawable.avatar).into(binding.imgDetails)
    }

    override fun initAction() {
        binding.imgBack.click {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.imgSaveImage.click {
            val bitmap = getImageOfView(binding.imgDetails)
            if (bitmap != null) {
                saveToStorage(bitmap)
            }
        }
    }

    private fun saveToStorage(bitmap: Bitmap) {
        val imageName = "image_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream?=null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity?.contentResolver?.also {contentResolver->
                val contentValue = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator+ "social media" + File.separator)
                }
                val imageUri: Uri?= contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
                fos = imageUri?.let {
                    contentResolver.openOutputStream(it)
                }
            }
        }else {
            val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDirectory, imageName)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            requireContext().showToasts("Downloaded", CustomToast.LONG)
        }

    }

    private fun getImageOfView(imgDetails: TouchImageView): Bitmap? {
        var image : Bitmap?=null
        try {
            image = Bitmap.createBitmap(imgDetails.measuredWidth, imgDetails.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            imgDetails.draw(canvas)

        }catch (e:Exception) {
            Log.d("ImageDetailsFragment", "Cannot Capture: ")
        }
        return image
    }

}