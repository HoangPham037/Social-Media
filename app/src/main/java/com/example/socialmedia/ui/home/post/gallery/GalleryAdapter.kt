package com.example.socialmedia.ui.home.post.gallery

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.databinding.ItemGallareyBinding
import com.example.socialmedia.model.ImageModel

class GalleryAdapter(val itemClick: ()-> Unit) : BaseRecyclerAdapter<ImageModel,GalleryAdapter.ViewHolder>() {
    var listItemSelected: ArrayList<ImageModel> = arrayListOf()
    inner  class ViewHolder (val binding : ViewDataBinding) : BaseViewHolder<ImageModel>(binding){
        override fun bind(itemData: ImageModel?) {
            super.bind(itemData)
            if (binding is ItemGallareyBinding) {
                Glide.with(itemView.context).load(itemData?.uri).into(binding.image)

                Glide.with(itemView.context)
                    .load(if(listItemSelected.any { it.uri == itemData?.uri }) R.drawable.ic_select else R.drawable.ic_checkbox)
                    .into(binding.imageCheck)
            }
            onItemClickListener {
                if (!listItemSelected.any { it.uri == itemData?.uri })
                    listItemSelected.add(listItem[adapterPosition])
                else listItemSelected.remove(listItem[adapterPosition])
                notifyItemChanged(adapterPosition)
                itemClick.invoke()
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_gallarey
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}