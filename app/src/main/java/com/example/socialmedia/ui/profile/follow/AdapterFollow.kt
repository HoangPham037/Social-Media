package com.example.socialmedia.ui.profile.follow

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.databinding.ItemFollowBinding
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOW
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWING


class AdapterFollow(
    val viewModel: FollowViewModel,
    var event: View.OnClickListener
) : BaseRecyclerAdapter<Profile, AdapterFollow.FollowViewHolder>() {
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
                binding.ivMenuFollow.tag = itemData
                binding.tvName.text = itemData?.name
                binding.tvFollow.text = itemData?.type
                binding.tvDescription.text = itemData?.description
                binding.tvDescription.goneIf(itemData?.description == null)
                setUpTextView(binding, itemData)
                Glide.with(binding.root.context).load(itemData?.avtPath)
                    .placeholder(R.drawable.aaa)
                    .fallback(R.drawable.avatar_uid_1).placeholder(R.drawable.aaa)
                    .fallback(R.drawable.avatar_uid_1).into(binding.ivAvt)
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
            if (itemData.id?.equals(viewModel.uRepository.getUid()) == true) {
                binding.tvFollow.visibility = View.GONE
                binding.ivMenuFollow.visibility = View.GONE
            }else{
                binding.tvFollow.visibility = View.VISIBLE
                binding.ivMenuFollow.visibility = View.VISIBLE
            }
            binding.ivMenuFollow.visibility =
                if (itemData.type == KEY_FOLLOW) View.VISIBLE else View.GONE
            binding.tvFollow.setBackgroundResource(if (itemData.isFollow) R.drawable.bg_textview else R.drawable.bg_textview1)
            binding.tvFollow.setTextColor(if (itemData.isFollow) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            if (itemData.type == KEY_FOLLOW) {
                binding.tvFollow.text = if (!itemData.isFollow) itemData.type else KEY_FOLLOWING
            } else {
                binding.tvFollow.text = if (itemData.isFollow) itemData.type else KEY_FOLLOW
            }
        }
    }
}


