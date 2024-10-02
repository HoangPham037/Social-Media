package com.example.socialmedia.ui.chat.chatscreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.common.State
import com.example.socialmedia.model.Conversion
import com.example.socialmedia.databinding.ItemMessageLayoutBinding
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserFragment
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.DateProvider
import com.example.socialmedia.ui.utils.formatReceiverMes
import com.example.socialmedia.ui.utils.getLastTimeAgo

class ChatAdapter(
    val uid: String,
    val context: Context,
    val chatViewModel: ChatViewModel,
    private val event: (Conversion) -> Unit
) : BaseRecyclerAdapter<Conversion, ChatAdapter.RecentConversionHolder>() {

    inner class RecentConversionHolder(val view: ViewDataBinding) :
        BaseViewHolder<Conversion>(view) {
        override fun bind(itemData: Conversion?) {
            super.bind(itemData)
            if (view is ItemMessageLayoutBinding) {
                itemView.setOnLongClickListener {
                    if (itemData != null) {
                        event.invoke(itemData)
                    }
                    true
                }
                itemData?.listUid?.let { listUser ->
                    chatViewModel.getOtherUserFromConversion(listUser) { state ->
                        when (state) {
                            is State.Success -> {
                                view.tvName.text = state.data.name
                                Glide.with(context).load(state.data.avtPath)
                                    .placeholder(R.drawable.avatar).fallback(R.drawable.avatar)
                                    .into(view.imgAvatar)
                                if (state.data.status == Constants.KEY_STATUS_ONLINE) {
                                    view.imgActiveState.visible()
                                } else {
                                    view.imgActiveState.gone()
                                }
                                onItemClickListener {
                                    val bundle = Bundle()
                                    bundle.putString(Constants.KEY_USER_ID, state.data.id)
                                    bundle.putString(
                                        Constants.KEY_LAST_MESS_SENDER_ID,
                                        itemData.lastMessageSenderId
                                    )
                                    (context as MainActivity).openFragment(
                                        ChatWithUserFragment::class.java,
                                        bundle,
                                        true
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
                val myUid = itemData?.listUid?.get(0)
                if (myUid == uid){
                    view.imgMute.goneIf(itemData.isMuteSend == false)
                }else {
                    view.imgMute.goneIf(itemData?.isMuteReversion == false)
                }

                val lastMessageSendByMe = itemData?.lastMessageSenderId.equals(uid)
                if (lastMessageSendByMe) {
                    val typeFaces = ResourcesCompat.getFont(context, R.font.montserrat)
                    with(view.tvLastMessage) {
                        text = itemData?.lastMessage?.formatReceiverMes()
                        setTextColor(ContextCompat.getColor(context, R.color.text_color))
                        typeface = typeFaces
                    }
                } else {
                    view.tvLastMessage.text = itemData?.lastMessage
                    if (itemData?.isSeen == true) {
                        val typeFace = ResourcesCompat.getFont(context, R.font.montserrat)
                        view.imgStateNewMessage.gone()
                        view.tvLastMessage.typeface = typeFace
                        view.tvLastMessage.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_color
                            )
                        )
                    } else {
                        val typeFace = ResourcesCompat.getFont(context, R.font.montserrat_bold)
                        view.tvLastMessage.typeface = typeFace
                        view.tvLastMessage.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        view.imgStateNewMessage.visible()
                    }
                }
                itemData?.lastMessageTimestamp?.let { timestamp ->
                    view.tvTimeSend.text =
                        getLastTimeAgo(DateProvider.convertTimestampsToLong(timestamp))
                }
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_message_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentConversionHolder {
        return RecentConversionHolder(getViewHolderDataBinding(parent, viewType))
    }
}