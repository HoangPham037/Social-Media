package com.example.socialmedia.ui.minigame.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.socialmedia.BR
import com.example.socialmedia.R

abstract class BaseActivity< VB : ViewDataBinding>: AppCompatActivity() {
    private lateinit var internalViewBinding : VB

    val viewBinding : VB
    get() = internalViewBinding


    lateinit var circleProgressDialog: CircleProgressDialog

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        circleProgressDialog = CircleProgressDialog(this)
        internalViewBinding = DataBindingUtil.setContentView(this, layoutId())

        internalViewBinding.apply {

            internalViewBinding.lifecycleOwner = this@BaseActivity
            root.isClickable = true
            executePendingBindings()
        }
        bindView()
        observeData()
        setDefaultView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setDefaultView() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    @LayoutRes
    abstract fun layoutId() : Int
    abstract fun bindView()
    abstract fun observeData()

}
