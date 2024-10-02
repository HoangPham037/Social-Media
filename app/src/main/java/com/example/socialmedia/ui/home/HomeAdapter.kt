package com.example.socialmedia.ui.home

import android.content.ContentValues.TAG
import android.util.Log
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.ItemNewsFeedBinding
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.ui.home.comment.ImageViewAdapter
import com.example.socialmedia.ui.utils.getLastTimeAgo
import com.example.socialmedia.ui.utils.setTextView

class HomeAdapter(val viewModel: HomeViewModel, val event : (typeClick: TypeClick, ItemHome)-> Unit) :
    BaseRecyclerAdapter<ItemHome, HomeAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<ItemHome>(binding) {
        override fun bind(itemData: ItemHome?) {
            if (binding is ItemNewsFeedBinding) {
                var itemHome = itemData
                if (itemHome != null) {
                    getUser(itemHome, binding)
                    addListenerItemHomeChange(itemHome){
                        itemHome = it
                        itemHome?.let {item -> setDataItemHome(item, binding, position) }
                    }
                    itemHome?.let { setDataItemHome(it, binding, position) }
                    binding.cvMedia.click {
                        itemHome?.let {  event.invoke(TypeClick.TYPE_IMAGE ,it)}
                    }
                    onItemClickListener {
                        itemHome?.let {  event.invoke(TypeClick.TYPE_IMAGE ,it)}
                    }
                }
            }
            binding.executePendingBindings()
        }
    }
    private fun getUser(itemHome : ItemHome , binding: ItemNewsFeedBinding){
        viewModel.getUser(itemHome.userid) {
            when (it) {
                is State.Success -> {
                    it.data.let {profile ->
                        Glide.with(binding.root).load(profile.avtPath).placeholder(R.drawable.avatar).fallback(R.drawable.avatar).into(binding.ivAvatar)
                        binding.tvUserName.text = profile.name
                    }
                }
                else -> {
                    Log.d(TAG, "bind: lỗi ")
                }
            }
        }
    }
    private fun addListenerItemHomeChange(itemHome: ItemHome,  callback: (ItemHome) -> Unit) {
        viewModel.addListenerItemHomeChange(itemHome) {
          callback.invoke(it)
        }
    }
    private fun setDataItemHome(itemHome: ItemHome, binding: ItemNewsFeedBinding, position: Int) {
        binding.tvDescription.text = itemHome.content
        binding.tvDescription.goneIf(itemHome.content.isNullOrEmpty())
        binding.tvActive.text = itemHome.datetime?.let { getLastTimeAgo(it) }
        binding.ivHeart.setImageResource(if (itemHome.listUserLike?.contains(viewModel.repository.getUid()) == true) R.drawable.ic_heart_true else R.drawable.ic_heart2)
        binding.tvHeart.text = setTextView(itemHome.listUserLike?.size)
        binding.ivHeart.click {
            viewModel.updatePostLike(itemHome)
        }
        binding.ivHeart.setImageResource(if (itemHome.listUserLike?.contains(viewModel.repository.getUid()) == true) R.drawable.ic_heart_true else R.drawable.ic_heart2)
        val adapter = ImageViewAdapter{
            event.invoke(TypeClick.TYPE_IMAGE,itemHome)
        }
        binding.ivAvatar.setOnClickListener { event.invoke(TypeClick.TYPE_AVT , itemHome) }
        binding.tvUserName.setOnClickListener { event.invoke(TypeClick.TYPE_AVT , itemHome) }
        binding.cvMedia.adapter = adapter
        adapter.setData(itemHome.urlImage.toMutableList() as ArrayList<String>)
        binding.cvMedia.goneIf(itemHome.urlImage.size== 0)
        binding.tvComment.text = itemHome.listCommnent.size.toString()
        binding.space.goneIf(position != listItem.size - 1)
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_news_feed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}
enum class TypeClick{
   TYPE_AVT , TYPE_IMAGE
}