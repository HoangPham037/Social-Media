package com.example.logoquiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.databinding.ItemButtonQuestionBinding
import com.example.socialmedia.model.SuggestModel

class GridViewSuggestAdapter
    : ListAdapter<SuggestModel, GridViewSuggestAdapter.ViewHolderSuggest>(DiffCallBackSuggest()) {
    var listener: OnClickItemSuggest? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSuggest {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemButtonQuestionBinding.inflate(inflater)
        return ViewHolderSuggest(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolderSuggest, position: Int) {
        holder.binDataSuggest(getItem(position), listener)
    }


    interface OnClickItemSuggest {
        fun onClickSuggest(position: Int, suggest: SuggestModel)
    }

    inner class ViewHolderSuggest(private val binding: ItemButtonQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binDataSuggest(suggest: SuggestModel, listener: OnClickItemSuggest?) {
            itemView.run {
                setOnClickListener {
                    listener?.onClickSuggest(adapterPosition, suggest)
                }
                if (!suggest.isCheck) {
                    binding.btnQuestion.text = suggest.labelSuggest
                } else {
                    binding.btnQuestion.text = " "
                }
            }
        }
    }

}

class DiffCallBackSuggest : DiffUtil.ItemCallback<SuggestModel>() {
    override fun areItemsTheSame(oldItem: SuggestModel, newItem: SuggestModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SuggestModel, newItem: SuggestModel): Boolean {
        return oldItem == newItem
    }

}