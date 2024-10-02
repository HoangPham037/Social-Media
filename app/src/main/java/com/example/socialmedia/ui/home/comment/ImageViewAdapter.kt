package com.example.socialmedia.ui.home.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.databinding.ItemFullScreenBinding
import com.example.socialmedia.databinding.ItemOneColumnBinding
import com.example.socialmedia.databinding.ItemTwoColumnsBinding
import com.example.socialmedia.databinding.ItemTwoRowsBinding
import com.example.socialmedia.model.ItemHome
import com.vanniktech.ui.click


class ImageViewAdapter(
    val typeDelete: Boolean = false,
    private val event: ((String) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listImageView: ArrayList<String> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val view = ItemFullScreenBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FullScreenViewHolder(view)
            }

            1 -> {
                val view = ItemTwoColumnsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TwoColumnsViewHolder(view)
            }
            2 -> {
                val view = ItemOneColumnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OneColumnViewHolder(view)
            }
            else -> {
                val view = ItemTwoRowsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TwoRowsViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FullScreenViewHolder -> {  holder
                if (listImageView.size > 0)
                    for (index in holder.ivImages.indices) {
                        Glide.with(holder.ivImages[index]).load(listImageView[index])
                            .into(holder.ivImages[index])
                        if (event != null) {
                            holder.ivImages[index].click {
                                event.invoke(listImageView[index])
                            }
                        }
                    }
                    holder.listImageViewDelete.forEach {
                        it.goneIf(!typeDelete)
                    }
            }

            is TwoColumnsViewHolder -> {
                holder
                if (listImageView.size > 0)
                    for (index in holder.ivImages.indices) {
                        Glide.with(holder.ivImages[index]).load(listImageView[index])
                            .into(holder.ivImages[index])
                        holder.ivImages[index].click { event?.invoke(listImageView[index]) }
                    }
                holder.listImagesDelete.forEach {
                    it.goneIf(!typeDelete)
                }
            }

            is OneColumnViewHolder -> { holder
                if (listImageView.size > 0)
                    for (index in holder.ivImages.indices) {
                        Glide.with(holder.ivImages[index]).load(listImageView[index])
                            .into(holder.ivImages[index])
                        holder.ivImages[index].click { event?.invoke(listImageView[index]) }
                    }
                holder.listImagesDelete.forEach {
                    it.goneIf(!typeDelete)
                }
            }

            else -> {
                val twoRowsViewHolder: TwoRowsViewHolder = holder as TwoRowsViewHolder
                if (listImageView.size > 4) {
                    twoRowsViewHolder.binding.tvImageview.visible()
                    twoRowsViewHolder.binding.tvImageview.text = "${listImageView.size - 4}+"
                }
                if (listImageView.size > 0)
                    for (index in holder.ivImages.indices) {
                        Glide.with(twoRowsViewHolder.ivImages[index]).load(listImageView[index])
                            .into(twoRowsViewHolder.ivImages[index])
                        holder.ivImages[index].click { event?.invoke(listImageView[index]) }
                    }
                holder.listImgDelete.forEach {
                    it.goneIf(!typeDelete)
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (listImageView.size) {
            1 -> 0
            2 -> 1
            3 -> 2
            else -> 3
        }
    }
    override fun getItemCount(): Int {
        return if (listImageView.size > 0) {
            1
        } else 0
    }

    class FullScreenViewHolder(binding: ItemFullScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val ivImages: List<ImageView> = listOf(binding.ivFull)
        val listImageViewDelete: List<ImageView> = listOf(binding.ivClose1)
    }

    class TwoColumnsViewHolder(binding: ItemTwoColumnsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val ivImages: List<ImageView> = listOf(binding.ivTwo1, binding.ivTwo2)
        val listImagesDelete: List<ImageView> = listOf(binding.ivClose1, binding.ivClose2)
    }

    class OneColumnViewHolder(var binding: ItemOneColumnBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val ivImages: List<ImageView> = listOf(binding.ivOne1, binding.ivOne2, binding.ivOne3)
        val listImagesDelete: List<ImageView> =
            listOf(binding.ivClose1, binding.ivClose2, binding.ivClose3)
    }

    class TwoRowsViewHolder(var binding: ItemTwoRowsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val ivImages: List<ImageView> =
            listOf(binding.ivTwo1, binding.ivTwo2, binding.ivTwo3, binding.ivTwo4)
        val listImgDelete: List<ImageView> =
            listOf(binding.ivClose, binding.ivClose2, binding.ivClose3, binding.ivClose4)
    }

    fun setData(list: ArrayList<String>) {
        this.listImageView = list
        notifyDataSetChanged()
    }
}
