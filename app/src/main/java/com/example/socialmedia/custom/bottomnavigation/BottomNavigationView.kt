package com.example.socialmedia.custom.bottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.socialmedia.R
import com.example.socialmedia.databinding.BottomNavigationViewBinding
import com.example.socialmedia.ui.utils.Constants.INDEX_0
import com.example.socialmedia.ui.utils.Constants.INDEX_1
import com.example.socialmedia.ui.utils.Constants.INDEX_2
import com.example.socialmedia.ui.utils.Constants.INDEX_3

class BottomNavigationView : FrameLayout {
    private lateinit var binding: BottomNavigationViewBinding
    private var onSelectItemChanged : ((Int) -> Unit)? = null
    private var onClickFloat : (()-> Unit)? = null
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    @SuppressLint("ResourceAsColor")
    private fun init() {
        binding = BottomNavigationViewBinding.inflate(LayoutInflater.from(context), null, false)
        addView(binding.root)
        changeSelectMenu(INDEX_0)
        onClickItemListener()
        binding.floatingActionButton.setOnClickListener{
            onClickFloat?.invoke()
        }

    }

     fun changeSelectMenu(position: Int) {
        when (position) {
            INDEX_0 -> {
                setViewSelect(binding.imageHome, binding.titleHome)
                unSelectView(binding.imageSearch, binding.titleSearch)
                unSelectView(binding.imageMessage, binding.titleMessage)
                unSelectView(binding.imageUser, binding.titleUser)
            }

            INDEX_1 -> {
                unSelectView(binding.imageHome, binding.titleHome)
                setViewSelect(binding.imageSearch, binding.titleSearch)
                unSelectView(binding.imageMessage, binding.titleMessage)
                unSelectView(binding.imageUser, binding.titleUser)
            }

            INDEX_2 -> {
                unSelectView(binding.imageHome, binding.titleHome)
                unSelectView(binding.imageSearch, binding.titleSearch)
                setViewSelect(binding.imageMessage, binding.titleMessage)
                unSelectView(binding.imageUser, binding.titleUser)
            }

            INDEX_3 -> {
                unSelectView(binding.imageHome, binding.titleHome)
                unSelectView(binding.imageSearch, binding.titleSearch)
                unSelectView(binding.imageMessage, binding.titleMessage)
                setViewSelect(binding.imageUser, binding.titleUser)
            }
        }
    }

    private fun setViewSelect(imageView: ImageView, text: TextView) {
        imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_orange_app));
        text.visibility = View.VISIBLE
    }

    private fun unSelectView(imageView: ImageView, text: TextView) {
        imageView.setColorFilter(ContextCompat.getColor(context, R.color.black));
        imageView.setColorFilter(R.color.black)
        text.visibility = View.GONE
    }
    private fun onClickItemListener(){
        binding.layoutHome.setOnClickListener { onSelectItemChanged?.invoke(INDEX_0) }
        binding.layoutSearch.setOnClickListener { onSelectItemChanged?.invoke(INDEX_1) }
        binding.layoutMessage.setOnClickListener { onSelectItemChanged?.invoke(INDEX_2) }
        binding.layoutUser.setOnClickListener { onSelectItemChanged?.invoke(INDEX_3) }
    }
    fun setOnClickItemClickListener(onClickItemClickListener: (Int)-> Unit){
        this.onSelectItemChanged = onClickItemClickListener
    }
    fun setOnClickFloat(onClickFloat: ()-> Unit){
        this.onClickFloat = onClickFloat
    }

}