package com.example.socialmedia.ui.chat.chatscreen.chatwithuser

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R
import com.example.socialmedia.base.recyclerview.BaseRecyclerAdapter
import com.example.socialmedia.base.recyclerview.BaseViewHolder
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.databinding.RowAudioReceiverBinding
import com.example.socialmedia.databinding.RowAudioSendBinding
import com.example.socialmedia.databinding.RowImgReceiveBinding
import com.example.socialmedia.databinding.RowImgSentBinding
import com.example.socialmedia.databinding.RowReceiveMesBinding
import com.example.socialmedia.databinding.RowSentMesBinding
import com.example.socialmedia.databinding.RowVideoReceiverBinding
import com.example.socialmedia.databinding.RowVideoSendBinding
import com.example.socialmedia.model.MessageModel
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.imagedetails.ImageDetailsFragment
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.videodetails.VideoDetailFragment
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.DateProvider
import com.example.socialmedia.ui.utils.formatLongToStringTimer
import java.io.IOException

class ChatWithUserAdapter(private val uid: String, val context: Context) :
    BaseRecyclerAdapter<MessageModel, BaseViewHolder<MessageModel>>() {

    private lateinit var timer: CountDownTimer

    companion object {
        private const val TYPE_SENT_MES = 0
        private const val TYPE_RECEIVER_MES = 1
        private const val TYPE_SENT_IMAGE = 2
        private const val TYPE_RECEIVER_IMAGE = 3
        private const val TYPE_SEND_AUDIO = 4
        private const val TYPE_RECEIVER_AUDIO = 5
        private const val TYPE_SEND_VIDEO = 6
        private const val TYPE_RECEIVER_VIDEO = 7
        private var player = MediaPlayer()
        private lateinit var exoPlayers: ExoPlayer
    }

    inner class TextSentHolder(val view: ViewDataBinding) : BaseViewHolder<MessageModel>(view) {
        override fun bind(itemData: MessageModel?) {
            super.bind(itemData)
            if (view is RowSentMesBinding) {
                val position = listItem.indexOf(itemData)
                val atTheSameDay = DateProvider.checkMessageDate(listItem, position)
                with(view) {
                    tvContent.text = itemData?.message
                    txtMessTime.text = DateProvider.formatTimestampsString(itemData?.timestamp!!)
                    tvSendConversationDate.apply {
                        text = if (!atTheSameDay) DateProvider.getChatTime(
                            DateProvider.convertTimestampsToLong(
                                itemData.timestamp!!
                            ).toString()
                        ) else ""
                        visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                    }
                    executePendingBindings()
                }
            }
        }
    }

    inner class TextReceiverHolder(val view: ViewDataBinding) : BaseViewHolder<MessageModel>(view) {
        override fun bind(itemData: MessageModel?) {
            super.bind(itemData)
            if (view is RowReceiveMesBinding) {
                with(view) {
                    val position = listItem.indexOf(itemData)
                    val atTheSameDay = DateProvider.checkMessageDate(listItem, position)
                    txtMsg.text = itemData?.message
                    txtMsgTime.text = DateProvider.formatTimestampsString(itemData?.timestamp!!)
                    tvSendConversationDate.apply {
                        text = if (!atTheSameDay) DateProvider.getChatTime(
                            DateProvider.convertTimestampsToLong(
                                itemData.timestamp!!
                            ).toString()
                        ) else ""
                        visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                    }
                    executePendingBindings()
                }
            }
        }
    }

    inner class ImgSentHolder(val view: ViewDataBinding) : BaseViewHolder<MessageModel>(view) {
        override fun bind(itemData: MessageModel?) {
            super.bind(itemData)
            if (view is RowImgSentBinding) {
                val position = listItem.indexOf(itemData)
                val atTheSameDay = DateProvider.checkMessageDate(listItem, position)
                Glide.with(context).load(itemData?.message).into(view.imgSend)
                with(view) {
                    txtMessTime.text = DateProvider.formatTimestampsString(itemData?.timestamp!!)
                    imgSend.click {
                        val bundle = Bundle()
                        bundle.putString("imgUrl", itemData.message)
                        (context as MainActivity).openFragment(
                            ImageDetailsFragment::class.java,
                            bundle,
                            true
                        )

                    }
                    tvSendConversationDate.apply {
                        text = if (!atTheSameDay) DateProvider.getChatTime(
                            DateProvider.convertTimestampsToLong(
                                itemData.timestamp!!
                            ).toString()
                        ) else ""
                        visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                    }
                    executePendingBindings()
                }
            }
        }
    }

    inner class ImgReceiverHolder(val view: ViewDataBinding) : BaseViewHolder<MessageModel>(view) {
        override fun bind(itemData: MessageModel?) {
            super.bind(itemData)
            if (view is RowImgReceiveBinding) {
                val position = listItem.indexOf(itemData)
                val atTheSameDay = DateProvider.checkMessageDate(listItem, position)
                Glide.with(context).load(itemData?.message).into(view.imgReceiver)
                with(view) {
                    txtMessTime.text = DateProvider.formatTimestampsString(itemData?.timestamp!!)
                    imgReceiver.click {
                        val bundle = Bundle()
                        bundle.putString("imgUrl", itemData.message)
                        (context as MainActivity).openFragment(
                            ImageDetailsFragment::class.java,
                            bundle,
                            true
                        )
                    }
                    tvSendConversationDate.apply {
                        text = if (!atTheSameDay) DateProvider.getChatTime(
                            DateProvider.convertTimestampsToLong(
                                itemData.timestamp!!
                            ).toString()
                        ) else ""
                        visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                    }
                    executePendingBindings()
                }
            }
        }
    }

    inner class AudioSendHolder(val view: ViewDataBinding) : BaseViewHolder<MessageModel>(view) {
        override fun bind(itemData: MessageModel?) {
            super.bind(itemData)
            if (view is RowAudioSendBinding) {
                val position = listItem.indexOf(itemData)
                val atTheSameDay = DateProvider.checkMessageDate(listItem, position)
                view.tvSendConversationDate.apply {
                    text = if (!atTheSameDay) DateProvider.getChatTime(
                        DateProvider.convertTimestampsToLong(
                            itemData?.timestamp!!
                        ).toString()
                    ) else ""
                    visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                }
                view.imgActionPlay.click {
                    if (player.isPlaying) {
                        player.apply {
                            stop()
                            reset()
                            timer.cancel()
                        }
                        view.playingAudio1.cancelAnimation()
                        view.playingAudio2.cancelAnimation()
                        view.imgActionPlay.setImageResource(R.drawable.ic_action_play_send)
                    } else {
                        player = MediaPlayer()
                        player.apply {
                            try {
                                setDataSource(context, Uri.parse(itemData?.message))
                                prepareAsync()
                                setOnPreparedListener {
                                    start()
                                    itemData?.duration?.let { durations ->
                                        startTimer(
                                            durations, view.tvTimer
                                        )
                                    }
                                    timer.start()
                                    view.imgActionPlay.setImageResource(R.drawable.ic_action_pause_send)
                                    view.playingAudio1.playAnimation()
                                    view.playingAudio2.playAnimation()
                                }
                                setOnCompletionListener {
                                    view.imgActionPlay.setImageResource(R.drawable.ic_action_play_send)
                                    view.playingAudio1.cancelAnimation()
                                    view.playingAudio2.cancelAnimation()
                                    view.tvTimer.text = itemData?.duration
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                view.tvTimer.text = itemData?.duration
                view.txtMessTime.text = DateProvider.formatTimestampsString(itemData?.timestamp!!)
                view.executePendingBindings()
            }
        }
    }

    inner class AudioReceiverHolder(val view: ViewDataBinding) :
        BaseViewHolder<MessageModel>(view) {
        override fun bind(itemData: MessageModel?) {
            super.bind(itemData)
            if (view is RowAudioReceiverBinding) {
                val position = listItem.indexOf(itemData)
                val atTheSameDay = DateProvider.checkMessageDate(listItem, position)
                view.tvSendConversationDate.apply {
                    text = if (!atTheSameDay) DateProvider.getChatTime(
                        DateProvider.convertTimestampsToLong(
                            itemData?.timestamp!!
                        ).toString()
                    ) else ""
                    visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                }
                view.imgActionPlay.click {
                    if (player.isPlaying) {
                        player.apply {
                            stop()
                            reset()
                            timer.cancel()
                        }
                        view.playingAudio1.cancelAnimation()
                        view.playingAudio2.cancelAnimation()
                        view.imgActionPlay.setImageResource(R.drawable.ic_action_play_receiver)
                    } else {
                        player = MediaPlayer()
                        player.apply {
                            try {
                                setDataSource(context, Uri.parse(itemData?.message))
                                prepareAsync()
                                setOnPreparedListener {
                                    start()
                                    itemData?.duration?.let { durations ->
                                        startTimer(
                                            durations, view.tvTimer
                                        )
                                    }
                                    timer.start()
                                    view.imgActionPlay.setImageResource(R.drawable.ic_action_pause_receiver)
                                    view.playingAudio1.playAnimation()
                                    view.playingAudio2.playAnimation()
                                }
                                setOnCompletionListener {
                                    view.imgActionPlay.setImageResource(R.drawable.ic_action_play_receiver)
                                    view.playingAudio1.cancelAnimation()
                                    view.playingAudio2.cancelAnimation()
                                    view.tvTimer.text = itemData?.duration
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                view.tvTimer.text = itemData?.duration
                view.txtMessTime.text = DateProvider.formatTimestampsString(itemData?.timestamp!!)
                view.executePendingBindings()
            }
        }
    }

    inner class VideoSendHolder(val view: ViewDataBinding) : BaseViewHolder<MessageModel>(view) {
        override fun bind(itemData: MessageModel?) {
            super.bind(itemData)
            if (view is RowVideoSendBinding) {
                val position = listItem.indexOf(itemData)
                val atTheSameDay = DateProvider.checkMessageDate(listItem, position)
                view.tvSendConversationDate.apply {
                    text = if (!atTheSameDay) DateProvider.getChatTime(
                        DateProvider.convertTimestampsToLong(
                            itemData?.timestamp!!
                        ).toString()
                    ) else ""
                    visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                }
                exoPlayers = ExoPlayer.Builder(context).build()
                view.videoSend.player = exoPlayers
                val mediaItem = itemData?.message?.let { MediaItem.fromUri(it) }
                if (mediaItem != null) {
                    exoPlayers.setMediaItem(mediaItem)
                    exoPlayers.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            when (playbackState) {
                                Player.STATE_ENDED -> {
                                    view.imgPlayVideo.visible()
                                    view.tvDuration.visible()
                                }
                                Player.STATE_READY -> {
                                    exoPlayers.let { player ->
                                        val duration = player.duration
                                        view.tvDuration.text = "00:${duration / 1000}"
                                    }
                                }
                                else -> {}
                            }
                        }
                    })
                    exoPlayers.prepare()
                    view.imgPlayVideo.click {
                        exoPlayers.seekTo(0)
                        exoPlayers.play()
                        view.imgPlayVideo.gone()
                        view.tvDuration.gone()
                    }
                }
                view.cardView.click {
                    val bundle = Bundle()
                    bundle.putString("videoUrl", itemData?.message)
                    (context as MainActivity).openFragment(
                        VideoDetailFragment::class.java,
                        bundle,
                        true
                    )
                }
                view.txtMessTime.text = DateProvider.formatTimestampsString(itemData?.timestamp!!)
                view.executePendingBindings()
            }
        }
    }

    inner class VideoReceiverHolder(val view: ViewDataBinding) :
        BaseViewHolder<MessageModel>(view) {
        override fun bind(itemData: MessageModel?) {
            super.bind(itemData)
            if (view is RowVideoReceiverBinding) {
                val position = listItem.indexOf(itemData)
                val atTheSameDay = DateProvider.checkMessageDate(listItem, position)
                view.tvSendConversationDate.apply {
                    text = if (!atTheSameDay) DateProvider.getChatTime(
                        DateProvider.convertTimestampsToLong(
                            itemData?.timestamp!!
                        ).toString()
                    ) else ""
                    visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                }
                exoPlayers = ExoPlayer.Builder(context).build()
                view.videoReceiver.player = exoPlayers
                val mediaItem = itemData?.message?.let { MediaItem.fromUri(it) }
                if (mediaItem != null) {
                    exoPlayers.setMediaItem(mediaItem)
                    exoPlayers.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            when(playbackState) {
                                Player.STATE_ENDED -> {
                                    view.imgPlayVideo.visible()
                                    view.tvDuration.visible()
                                }
                                Player.STATE_READY -> {
                                    exoPlayers.let { player ->
                                        val duration = player.duration
                                        view.tvDuration.text = "00:${duration / 1000}"
                                    }
                                }
                                else -> {}
                            }
                        }
                    })
                    exoPlayers.prepare()
                    view.imgPlayVideo.click {
                        exoPlayers.seekTo(0)
                        exoPlayers.play()
                        view.imgPlayVideo.gone()
                        view.tvDuration.gone()
                    }
                }
                view.cardView.click {
                    val bundle = Bundle()
                    bundle.putString("videoUrl", itemData?.message)
                    (context as MainActivity).openFragment(
                        VideoDetailFragment::class.java,
                        bundle,
                        true
                    )
                }
                view.txtMessTime.text = DateProvider.formatTimestampsString(itemData?.timestamp!!)
                view.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BaseViewHolder<MessageModel> {
        return when (viewType) {
            TYPE_SENT_MES -> {
                TextSentHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_RECEIVER_MES -> {
                TextReceiverHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_SENT_IMAGE -> {
                ImgSentHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_RECEIVER_IMAGE -> {
                ImgReceiverHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_SEND_AUDIO -> {
                AudioSendHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_RECEIVER_AUDIO -> {
                AudioReceiverHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_SEND_VIDEO -> {
                VideoSendHolder(getViewHolderDataBinding(parent, viewType))
            }

            TYPE_RECEIVER_VIDEO -> {
                VideoReceiverHolder(getViewHolderDataBinding(parent, viewType))
            }

            else -> {
                throw Exception("Error reading holder type")
            }
        }

    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return when (viewType) {
            TYPE_SENT_MES -> {
                R.layout.row_sent_mes
            }

            TYPE_RECEIVER_MES -> {
                R.layout.row_receive_mes
            }

            TYPE_SENT_IMAGE -> {
                R.layout.row_img_sent
            }

            TYPE_RECEIVER_IMAGE -> {
                R.layout.row_img_receive
            }

            TYPE_SEND_AUDIO -> {
                R.layout.row_audio_send
            }

            TYPE_RECEIVER_AUDIO -> {
                R.layout.row_audio_receiver
            }

            TYPE_SEND_VIDEO -> {
                R.layout.row_video_send
            }

            TYPE_RECEIVER_VIDEO -> {
                R.layout.row_video_receiver
            }

            else -> {
                throw Exception("Error get item layout resource")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = listItem[position]
        return if (item.senderId.equals(uid)) {
            when (item.typeMessage) {
                Constants.TEXT -> TYPE_SENT_MES
                Constants.IMAGE -> TYPE_SENT_IMAGE
                Constants.VIDEO -> TYPE_SEND_VIDEO
                else -> TYPE_SEND_AUDIO
            }
        } else {
            when (item.typeMessage) {
                Constants.TEXT -> TYPE_RECEIVER_MES
                Constants.IMAGE -> TYPE_RECEIVER_IMAGE
                Constants.VIDEO -> TYPE_RECEIVER_VIDEO
                else -> TYPE_RECEIVER_AUDIO
            }
        }
    }

    private fun startTimer(duration: String, textTimer: TextView) {
        val minutes = duration.split(":")[0].toInt()
        val seconds = duration.split(":")[1].toInt()
        val totalSeconds = (minutes * 60) + seconds
        timer = object : CountDownTimer(totalSeconds * 1000L, 1000L) {
            override fun onTick(p0: Long) {
                val remainingSeconds = (p0 + 1) / 1000L
                val minute = remainingSeconds / 60
                val second = remainingSeconds % 60
                textTimer.text = formatLongToStringTimer(minute, second)
            }

            override fun onFinish() {
                /** do nothing **/
            }
        }
    }
}