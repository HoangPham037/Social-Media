
package com.example.socialmedia.base

import android.os.Build
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

abstract class PermissionFragment<VB : ViewBinding> : BaseFragmentWithBinding<VB>() {

     var arrPermissions = if (Build.VERSION.SDK_INT >= 33) arrayOf(
        "android.permission.READ_MEDIA_IMAGES"
    ) else arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
    )


    open fun openGallery(callback : ()->Unit) {
        Dexter.withContext(context).withPermissions(*arrPermissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) {
                        callback.invoke()
                }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show()
            }.onSameThread().check()
    }
}
