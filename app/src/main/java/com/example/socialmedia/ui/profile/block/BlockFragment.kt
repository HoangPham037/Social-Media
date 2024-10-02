package com.example.socialmedia.ui.profile.block

import android.view.LayoutInflater
import android.view.View
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.databinding.FragmentBlockBinding
import com.example.socialmedia.model.Profile

import com.example.socialmedia.ui.profile.follow.FollowViewModel
import com.example.socialmedia.ui.utils.Constants.KEY_PROFILE
import org.koin.android.ext.android.inject

class BlockFragment : BaseFragmentWithBinding<FragmentBlockBinding>(), View.OnClickListener {
    private val viewModel: FollowViewModel by inject()
    private var adapter: AdapterBlock? = null
    override fun init() {
        val bundle = arguments ?: return
        val profile = bundle.getSerializable(KEY_PROFILE) as Profile
        if (profile.listBlock != null && profile.listBlock?.isNotEmpty() == true) {
            viewModel.getListBlock(profile)
        }
        adapter = AdapterBlock { view -> onClick(view) }
        binding.rcvBlock.adapter = adapter
    }

    override fun initData() {
        viewModel.listBlock.observe(viewLifecycleOwner) { listBlock ->
            adapter?.submitList(listBlock)
            if (listBlock != null) visibility(View.GONE) else visibility(View.VISIBLE)
        }
    }

    private fun visibility(isVisible: Int) {
        binding.tvBlock.visibility = isVisible
        binding.ivBlock.visibility = isVisible
    }


    override fun initAction() {
        binding.ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> onBackPressed()
            R.id.tv_follow -> unBlock(v)
        }
    }

    private fun unBlock(view: View) {
        val profile = view.tag as Profile
        viewModel.blockUsers(profile)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentBlockBinding {
        return FragmentBlockBinding.inflate(layoutInflater)
    }
}