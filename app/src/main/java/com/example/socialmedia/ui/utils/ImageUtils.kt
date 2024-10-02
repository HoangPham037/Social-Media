package com.example.socialmedia.ui.utils

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.CustomDialogSettingBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


object ImageUtils {
    fun loadImages(fragment: Fragment) {
        val arrayPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            ) else arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

        Dexter.withContext(fragment.requireContext())
            .withPermissions(*arrayPermission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) {
                        val intent = Intent()
                        intent.action = Intent.ACTION_GET_CONTENT
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                        fragment.startActivityForResult(
                            intent,
                            Constants.REQUEST_CODE_GET_IMAGE_FROM_GALLERY
                        )
                    }

                    if (p0.isAnyPermissionPermanentlyDenied) {
                        showSettingDialog(fragment)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(fragment.requireContext(), "Error occurred!", Toast.LENGTH_SHORT)
                    .show()
            }.onSameThread().check()
    }

    fun showSettingDialog(fragment: Fragment) {
        val dialogSetting = Dialog(fragment.requireContext())
        val dialogBinding = CustomDialogSettingBinding.inflate(fragment.layoutInflater)
        dialogSetting.setContentView(dialogBinding.root)
        dialogSetting.setCancelable(false)
        with(dialogBinding) {
            tvConfirm.click {
                openSetting(fragment)
                dialogSetting.dismiss()
            }
            tvConfirmCancel.click {
                dialogSetting.dismiss()
            }
        }
        dialogSetting.show()
    }

    private fun openSetting(fragment: Fragment) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", fragment.activity?.packageName, null)
        intent.setData(uri)
        fragment.startActivityForResult(intent, Constants.REQUEST_CODE_OPEN_SETTING)
    }

    fun takePhotoFromCamera(fragment: Fragment) {
        Dexter.withContext(fragment.requireContext())
            .withPermissions(Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "New Picture")
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        fragment.startActivityForResult(
                            cameraIntent,
                            Constants.REQUEST_CODE_TAKE_PHOTO_FROM_CAMERA
                        )
                    }

                    if (p0.isAnyPermissionPermanentlyDenied) {
                        showSettingDialog(fragment)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(fragment.requireContext(), "Error occurred!", Toast.LENGTH_SHORT)
                    .show()
            }.onSameThread().check()
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val ops = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ops)
        return ops.toByteArray()
    }
}