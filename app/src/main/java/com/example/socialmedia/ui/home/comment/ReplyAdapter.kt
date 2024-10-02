package com.example.socialmedia.ui.home.comment

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.ItemReplyBinding
import com.example.socialmedia.model.ReplyComment
import com.example.socialmedia.ui.utils.getLastTimeAgo

class ReplyAdapter(private val viewmodel: CommentViewmodel, val event : View.OnClickListener) : BaseRecyclerAdapter<ReplyComment, ReplyAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<ReplyComment>(binding) {
        override fun bind(itemData: ReplyComment?) {
            super.bind(itemData)
            if (binding is ItemReplyBinding) {
                viewmodel.getProfileUser(itemData?.uid){profile ->
                    when(profile){
                        is State.Success -> {
                            binding.tvName.text = profile.data.name
                            Glide.with(itemView.context).load(profile.data.avtPath).placeholder(R.drawable.avatar)
                                .fallback(R.drawable.avatar).into(binding.ivAvt)
                        }
                        else -> {

                        }
                    }
                }
                binding.ivComment.tag = itemData
                binding.ivHeart.tag = itemData
                binding.ivAvt.tag = itemData
                binding.tvName.tag = itemData
                binding.ivHeart.setOnClickListener { event.onClick(it) }
                binding.ivAvt.setOnClickListener { event.onClick(it) }
                binding.ivComment.setOnClickListener { event.onClick(it) }
                binding.tvName.setOnClickListener { event.onClick(it) }
                binding.tvTime.text = itemData?.dateTime?.let { getLastTimeAgo(it) }
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_reply
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}