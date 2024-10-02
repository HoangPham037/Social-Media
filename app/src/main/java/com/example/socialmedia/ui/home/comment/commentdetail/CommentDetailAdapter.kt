package com.example.socialmedia.ui.home.comment.commentdetail

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.databinding.ItemImageViewPostBinding
import com.example.socialmedia.ui.home.comment.CommentViewmodel

class CommentDetailAdapter(val viewmodel: CommentViewmodel, val event: View.OnClickListener) :
    BaseRecyclerAdapter<String, CommentDetailAdapter.ViewHolder>() {
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_image_view_post
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<String>(binding) {
        override fun bind(itemData: String?) {
            super.bind(itemData)
            if (binding is ItemImageViewPostBinding) {
                if (itemData != null) {
                    Glide.with(binding.root.context).load(itemData).placeholder(R.drawable.aaa)
                        .fallback(R.drawable.avatar_uid_1).into(binding.ivImageView)
                    binding.ivImageView.tag = itemData
                    binding.ivImageView.setOnClickListener { event.onClick(it) }
                }
            }
        }
    }
}