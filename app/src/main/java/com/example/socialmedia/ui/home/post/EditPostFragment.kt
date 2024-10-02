package com.example.socialmedia.ui.home.post

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.databinding.FragmentEditPostBinding
import com.example.socialmedia.databinding.FragmentPostBinding
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.ui.home.comment.ImageViewAdapter
import com.example.socialmedia.ui.home.post.gallery.GalleryImageFragment
import com.example.socialmedia.ui.utils.Constants
import com.facebook.AccessTokenManager.Companion.TAG
import com.nds.connectinglonelydays.service.PostService
import org.koin.android.ext.android.inject


class EditPostFragment : BaseFragmentWithBinding<FragmentEditPostBinding>() {

    companion object {
        fun newInstance() = EditPostFragment()
    }

    private lateinit var postAdapter: ImageViewAdapter
    private val viewModel: PostViewModel by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentEditPostBinding {
        return FragmentEditPostBinding.inflate(inflater)
    }


    override fun init() {
        postAdapter = ImageViewAdapter{
            openFragment(GalleryImageFragment::class.java, null, true)
        }
        binding.rcView.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.rcView.adapter = postAdapter
        if (arguments != null){
            viewModel.itemHome = arguments?.getSerializable(Constants.KEY_ITEM_HOME) as ItemHome
            viewModel.getUser(viewModel.itemHome?.userid)
            postAdapter.setData(viewModel.itemHome?.urlImage as ArrayList<String>)
            binding.editText.setText(viewModel.itemHome?.content)
        }
    }

    override fun initData() {
        viewModel.proFile.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it.avtPath).placeholder(R.drawable.avatar)
                .fallback(R.drawable.avatar).into(binding.imgAvatar)
            binding.tvUserName.text = it.name
        }
        (activity as MainActivity).viewmodel.listImage.observe(viewLifecycleOwner) {
            val list = arrayListOf<String>()
            it.forEach { item -> list.add(item.uri) }
            postAdapter.setData(list)
        }
    }



    override fun initAction() {
        binding.ivThumb.click {
            openFragment(GalleryImageFragment::class.java, null, true)
        }
        binding.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.checkData(text.toString())
        }
        binding.ivBack.click {
            onBackPressed()
        }
        binding.post.click {
            val data = (activity as MainActivity).viewmodel.listImage.value
            context?.startService(Intent(requireContext(), PostService::class.java).apply {
                if (data != null)
                    putExtra("image", data)
                putExtra("content",binding.editText.text.toString())
            })
            (activity as MainActivity).viewmodel.setListImage(arrayListOf())
            onBackPressed()
        }
    }
}