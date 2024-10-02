package com.example.socialmedia.ui.profile

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.databinding.ItemImageBinding
import com.example.socialmedia.model.ImageModel

class ChoosePictureAdapter(val itemClick: (ImageModel) -> Unit) :
    BaseRecyclerAdapter<ImageModel, ChoosePictureAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<ImageModel>(binding) {
        override fun bind(itemData: ImageModel?) {
            super.bind(itemData)
            if (binding is ItemImageBinding) {
                Glide.with(itemView.context).load(itemData?.uri).into(binding.ivImage)
                onItemClickListener {
                    itemData?.let { itemClick.invoke(it) }
                }
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}