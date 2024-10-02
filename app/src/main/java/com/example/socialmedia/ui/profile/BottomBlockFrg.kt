package com.example.socialmedia.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.model.Profile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomBlockFrg(val  event : View.OnClickListener) : BottomSheetDialogFragment() {
    private var profile : Profile? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_block, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBlock = view.findViewById<Button>(R.id.btn_block)
        val ivAvatar = view.findViewById<ImageView>(R.id.iv_avt)
        val tvContent = view.findViewById<TextView>(R.id.tv_content)
        tvContent.text = String.format("Block ${profile?.name} ??")
        Glide.with(requireContext()).load(profile?.avtPath).placeholder(R.drawable.aaa)
            .fallback(R.drawable.avatar_uid_1).into(ivAvatar)
        btnBlock.setOnClickListener {
            dismiss()
            event.onClick(it)
        }
    }
    fun setData(profile: Profile){
        this.profile = profile
    }
}