package com.example.socialmedia.ui.chat.chatscreen.chatwithuser.videodetails

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentVideoDetailBinding
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class VideoDetailFragment : BaseFragmentWithBinding<FragmentVideoDetailBinding>() {
    override fun getViewBinding(inflater: LayoutInflater): FragmentVideoDetailBinding {
        return FragmentVideoDetailBinding.inflate(layoutInflater)
    }

    override fun init() {}

    override fun initData() {
        val videoUrl = arguments?.getString("videoUrl") ?: ""
        val exoPlayer = ExoPlayer.Builder(requireActivity()).build()
        binding.videoSend.player = exoPlayer
        if (videoUrl != "") {
            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    override fun initAction() {
        binding.imgClose.click {
            onBackPressed()
        }
    }
}