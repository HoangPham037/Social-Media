package com.example.socialmedia.ui.profile

import android.content.ContentValues
import android.util.Log
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.ItemPostProfileBinding
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.ui.home.comment.ImageViewAdapter
import com.example.socialmedia.ui.profile.follow.FollowViewModel
import com.example.socialmedia.ui.utils.getLastTimeAgo
import com.example.socialmedia.ui.utils.setTextView
import com.google.firebase.appcheck.internal.util.Logger
import com.google.firebase.appcheck.internal.util.Logger.TAG

class PostAdapter(val viewModel: FollowViewModel, val onClick : (typeClick: TypeClick, Any)-> Unit) :
    BaseRecyclerAdapter<ItemHome, PostAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<ItemHome>(binding) {
        override fun bind(itemData: ItemHome?) {
            super.bind(itemData)
            if (binding is ItemPostProfileBinding) {
                var itemHome = itemData
                if (itemHome != null) {
                    getUser(itemHome, binding)
                    addListenerItemHomeChange(itemHome){
                        itemHome = it
                        setDataItemHome(itemHome!!, binding, position)
                    }
                    setDataItemHome(itemHome!!, binding, position)
                }
            }
            binding.executePendingBindings()
        }
    }

    private fun setDataItemHome(itemHome: ItemHome, binding: ItemPostProfileBinding, position: Int) {
        binding.tvDescription.text = itemHome.content
        binding.tvDescription.goneIf(itemHome.content.isNullOrEmpty())
        binding.tvTime.text = itemHome.datetime?.let { getLastTimeAgo(it) }
        binding.ivHeart.setImageResource(if (itemHome.listUserLike.contains(viewModel.uRepository.getUid())) R.drawable.ic_heart_true else R.drawable.ic_heart2)
        binding.tvHeart.text = setTextView(itemHome.listUserLike.size)
        binding.ivHeart.click { viewModel.updatePostLike(itemHome) }
        val adapterImageView = ImageViewAdapter{
            onClick.invoke(TypeClick.TYPE_IMAGE , itemHome)
        }
        adapterImageView.setData(itemHome.urlImage.toMutableList() as ArrayList<String>)
        binding.ivMore.tag = itemHome
        binding.rcvImageview.adapter = adapterImageView
        binding.rcvImageview.goneIf(itemHome.urlImage.isEmpty())
        binding.tvComment.text = setTextView(itemHome.listCommnent.size)
        binding.space.goneIf(position != listItem.size - 1)
        binding.ivAvt.click { onClick.invoke(TypeClick.TYPE_AVT,itemHome) }
        binding.ivMore.click { onClick.invoke(TypeClick.TYPE_MORE ,it) }
        binding.ivComment.click { onClick.invoke(TypeClick.TYPE_COMMENT , itemHome) }
        binding.tbName.click { onClick.invoke(TypeClick.TYPE_COMMENT,itemHome) }
        binding.tvDescription.click { onClick.invoke(TypeClick.TYPE_COMMENT,itemHome) }

    }

    private fun addListenerItemHomeChange(itemHome: ItemHome,  callback: (ItemHome) -> Unit) {
        viewModel.addListenerItemHomeChange(itemHome) {
            callback.invoke(it)
        }
    }

    private fun getUser(itemData: ItemHome, binding: ItemPostProfileBinding) {
        viewModel.getProfileUser(itemData.userid){
            when (it) {
                is State.Success -> {
                    it.data.let {profile ->
                        binding.tvName.text =profile.name
                        if(profile.avtPath!=null) {
                            Glide.with(binding.root.context).load(profile.avtPath).into(binding.ivAvt)
                        } else{
                            Glide.with(binding.root.context).load(R.drawable.ic_profile).into(binding.ivAvt)

                        }
                      /*  Glide.with(binding.root.context).load(profile.avtPath).placeholder(R.drawable.aaa)
                            .fallback(R.drawable.ic_profile).into(binding.ivAvt)*/
                        if (profile.id?.equals(viewModel.uRepository.getUid()) == true) binding.ivMore.visible() else binding.ivMore.gone()
                    }
                }
                else -> {
                    Log.d(ContentValues.TAG, "bind: lỗi ")
                }
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_post_profile
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}
enum class TypeClick{
    TYPE_MORE, TYPE_COMMENT ,TYPE_AVT , TYPE_IMAGE
}