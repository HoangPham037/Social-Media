package com.example.logoquiz.adapter

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("loadImage")
fun ImageView.loadImage(imgUrl : String?){
    Glide.with(context).load(Uri.parse("file:///android_asset/home/$imgUrl")).into(this)
}

@BindingAdapter("loadImageQuestion")
fun ImageView.loadImageQuestion(uri: String?){
    Glide.with(context).load(Uri.parse("file:///android_asset/image/$uri")).into(this)
}

@BindingAdapter("formatStringProcess")
fun formatStringProcess(view : TextView, newNumber : Int, oldNumber :Int){
   view.text = (newNumber / oldNumber*100).toString()
}