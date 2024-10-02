package com.example.socialmedia.ui.chat.chatscreen.searchconversion

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.databinding.ItemRecentSearchesBinding
import com.example.socialmedia.model.Profile

class SearchHistoryAdapter(private val context: Context, private val event: (Profile) -> Unit) :
    BaseRecyclerAdapter<Profile, SearchHistoryAdapter.SearchHistoryViewHolder>() {

    inner class SearchHistoryViewHolder(val view: ViewDataBinding) : BaseViewHolder<Profile>(view) {
        override fun bind(itemData: Profile?) {
            super.bind(itemData)
            if (view is ItemRecentSearchesBinding) {
                view.tvName.text = itemData?.name
                Glide.with(context).load(itemData?.avtPath).into(view.imgAvatar)
                onItemClickListener {
                    if (itemData != null) {
                        event.invoke(itemData)
                    }
                }
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_recent_searches
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(getViewHolderDataBinding(parent, viewType))
    }


}