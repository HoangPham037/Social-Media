package com.example.socialmedia.ui.setting

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.databinding.ItemUserBinding
import com.example.socialmedia.model.Profile


class AdapterUser(
    var event: View.OnClickListener
) : BaseRecyclerAdapter<Profile, AdapterUser.UserViewHolder>() {
    private var listDefault: ArrayList<Profile> = arrayListOf()
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    fun setData(listUser: ArrayList<Profile>) {
        submitList(listUser)
        listDefault = listUser
    }
    inner class UserViewHolder(val binding: ViewDataBinding) : BaseViewHolder<Profile>(binding) {
        @SuppressLint("NotifyDataSetChanged")
        override fun bind(itemData: Profile?) {
            super.bind(itemData)
            if (binding is ItemUserBinding) {
                binding.tvName.text = itemData?.name
                binding.itemUser.tag = itemData
                Glide.with(binding.root.context).load(itemData?.avtPath).placeholder(R.drawable.aaa)
                    .fallback(R.drawable.avatar_uid_1).into(binding.ivAvt)
                binding.itemUser.setOnClickListener { event.onClick(binding.itemUser) }
            }
            binding.executePendingBindings()
        }
    }
}

