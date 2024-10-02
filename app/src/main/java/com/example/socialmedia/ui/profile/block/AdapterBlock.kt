package com.example.socialmedia.ui.profile.block

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.databinding.ItemFollowBinding
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.utils.Constants.KEY_BLOCK
import com.example.socialmedia.ui.utils.Constants.KEY_UN_BLOCK


class AdapterBlock(
    var event: View.OnClickListener
) : BaseRecyclerAdapter<Profile, AdapterBlock.FollowViewHolder>() {
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_follow
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        return FollowViewHolder(getViewHolderDataBinding(parent, viewType))
    }
    inner class FollowViewHolder(val binding: ViewDataBinding) : BaseViewHolder<Profile>(binding) {
        @SuppressLint("NotifyDataSetChanged")
        override fun bind(itemData: Profile?) {
            super.bind(itemData)
            if (binding is ItemFollowBinding) {
                binding.tvFollow.tag = itemData
                binding.itemFollow.tag = itemData
                binding.tvName.text = itemData?.name
                binding.tvDescription.text = itemData?.description
                binding.ivMenuFollow.visibility = View.GONE
                setUpTextView(binding, itemData)
                Glide.with(binding.root.context).load(itemData?.avtPath).placeholder(R.drawable.aaa)
                    .fallback(R.drawable.avatar).into(binding.ivAvt)
                binding.tvFollow.setOnClickListener {
                    event.onClick(it)
                    if (itemData != null) itemData.isFollow = !itemData.isFollow
                    notifyDataSetChanged()
                }
                binding.ivMenuFollow.setOnClickListener { event.onClick(it) }
                binding.itemFollow.setOnClickListener { event.onClick(it) }
            }
            binding.executePendingBindings()
        }

        private fun setUpTextView(binding: ItemFollowBinding, itemData: Profile?) {
            if (itemData?.isFollow == null) return
            binding.tvFollow.setBackgroundResource(if (!itemData.isFollow) R.drawable.bg_round_boder_btn else R.drawable.bg_textview)
            binding.tvFollow.setTextColor(if (!itemData.isFollow) android.graphics.Color.WHITE else android.graphics.Color.BLACK)
            binding.tvFollow.text = if (itemData.isFollow) KEY_BLOCK else KEY_UN_BLOCK
        }
    }
}

