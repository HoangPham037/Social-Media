package com.example.socialmedia.ui.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.socialmedia.R

class CustomToast(context: Context?) : Toast(context) {
    companion object {
        const val LONG: Long = 7000
        const val SHORT: Long = 4000
        fun makeText(context: Context?, message: String, duration: Int): Toast {
            val toast = Toast(context)
            toast.duration = duration
            val layout = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null, false)
            val linearLayout = layout.findViewById<LinearLayout>(R.id.layoutToast)
            val textToast = layout.findViewById<TextView>(R.id.tvToast)
            val imageToast = layout.findViewById<ImageView>(R.id.imgToast)
            textToast.text = message
            linearLayout.setBackgroundResource(R.drawable.success_shape)
            imageToast.setImageResource(R.drawable.ic_success)
            toast.view = layout
            return toast
        }
    }


}