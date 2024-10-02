package com.example.socialmedia.ui.profile.account

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.databinding.ItemAccountBinding

class AdapterAccount(var  event : View.OnClickListener) : BaseRecyclerAdapter<Account, AdapterAccount.AccountViewHolder>() {
    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_account
    }
    fun setData(listAccount: ArrayList<Account>) {
        submitList(listAccount)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAccount.AccountViewHolder {
        return AccountViewHolder(getViewHolderDataBinding(parent, viewType))
    }
    inner class AccountViewHolder(val binding: ViewDataBinding) : BaseViewHolder<Account>(binding) {
        override fun bind(itemData: Account?) {
            if (binding is ItemAccountBinding) {
                itemView.tag = itemData
                binding.tvUsername.text = itemData?.tvName
                binding.tvContent.text = itemData?.tvContent
                itemData?.id?.let { binding.ivAvt.setBackgroundResource(it) }
                itemView.setOnClickListener {
                    event.onClick(itemView)
                }
            }
        }
    }
}