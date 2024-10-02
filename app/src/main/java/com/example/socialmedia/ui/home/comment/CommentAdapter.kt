package com.example.socialmedia.ui.home.comment

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.ItemCommentBinding
import com.example.socialmedia.model.Comment
import com.example.socialmedia.ui.utils.getLastTimeAgo
import com.example.socialmedia.ui.utils.setTextView

class CommentAdapter(val viewModel: CommentViewmodel, val event: View.OnClickListener) :
    BaseRecyclerAdapter<Comment, CommentAdapter.ViewHolder>() {
    private var listComment: HashMap<String, Comment>? = null
    private var itemComment: Comment? = null

    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<Comment>(binding) {
        override fun bind(itemData: Comment?) {
            super.bind(itemData)
            if (binding is ItemCommentBinding) {
                binding.ivComment.tag = itemData
                binding.ivHeart.tag = itemData
                binding.ivAvt.tag = itemData
                binding.tvName.tag = itemData
                binding.ivMoreComment.tag = itemData
                viewModel.getProfileUser(itemData?.userId) { profile ->
                    when (profile) {
                        is State.Success -> {
                            binding.tvName.text = profile.data.name
                            Glide.with(itemView.context).load(profile.data.avtPath)
                                .fallback(R.drawable.avatar)
                                .into(binding.ivAvt)
                        }

                        else -> {

                        }
                    }
                }
                val id = viewModel.uRepository.getUid()
                val boolean = itemData?.userId?.equals(id) == true
                binding.ivMoreComment.visibility = if (boolean) View.VISIBLE else View.GONE
                binding.tvContent.text = itemData?.title
                binding.tvTime.text = itemData?.datetime?.let { getLastTimeAgo(it) }
                binding.tvHeart.text = setTextView(itemData?.listLikeComment?.size)
                binding.tvComment.text = setTextView(itemData?.listReplyComment?.size)
                binding.ivComment.setOnClickListener { event.onClick(it) }
                binding.ivHeart.setOnClickListener {
                    itemComment = listItem[adapterPosition]
                    event.onClick(it)
                }

                binding.tvName.setOnClickListener {
                    itemComment = listItem[adapterPosition]
                    event.onClick(it)
                }
                binding.ivAvt.setOnClickListener {
                    itemComment = listItem[adapterPosition]
                    event.onClick(it)
                }
                binding.ivMoreComment.setOnClickListener {
                    itemComment = listItem[adapterPosition]
                    event.onClick(it)
                }
                val uid = viewModel.uRepository.getUid()
                if (uid != null) binding.ivHeart.setImageResource(
                    if (isLiked(
                            uid,
                            itemData
                        )
                    ) R.drawable.ic_heart_true else R.drawable.ic_heart2
                )
                val adapter = ReplyAdapter(viewModel) {
                    event.onClick(it)
                }
                binding.revReply.adapter = adapter
                adapter.submitList(itemData?.listReplyComment?.values?.toList())
            }
        }

        private fun isLiked(uid: String, itemData: Comment?): Boolean {
            val listLike = listComment?.get(itemData?.key)?.listLikeComment
            return listLike?.contains(uid) ?: false
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_comment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }

    fun setData(listData: HashMap<String, Comment>?) {
        submitList(listData?.values?.toList()?.sortedBy { it.datetime })
        this.listComment = listData
    }
}