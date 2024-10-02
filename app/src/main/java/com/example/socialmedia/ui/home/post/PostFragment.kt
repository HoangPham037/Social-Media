package com.example.socialmedia.ui.home.post

import android.content.Intent
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.databinding.FragmentPostBinding
import com.example.socialmedia.ui.home.comment.ImageViewAdapter
import com.example.socialmedia.ui.home.post.gallery.GalleryImageFragment
import com.nds.connectinglonelydays.service.PostService
import org.koin.android.ext.android.inject


class PostFragment : BaseFragmentWithBinding<FragmentPostBinding>() {

    companion object {
        fun newInstance() = PostFragment()
    }

    private lateinit var postAdapter: ImageViewAdapter
    private val viewModel: PostViewModel by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentPostBinding {
        return FragmentPostBinding.inflate(inflater)
    }


    override fun init() {
       // showLoadingDialog()
        viewModel.getUser(viewModel.repository.getUid())
        postAdapter = ImageViewAdapter{
            openFragment(GalleryImageFragment::class.java, null, true)
        }
        binding.rcView.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.rcView.adapter = postAdapter
        binding.materialToolbar.setNavigationOnClickListener {
            (activity as MainActivity).viewmodel.setListImage(arrayListOf())
            onBackPressed()
        }
    }

    private fun setUpTextView(color: Int) {
        binding.post.setTextColor(color)
    }

    override fun initData() {
        viewModel.isEnabledPost.observe(viewLifecycleOwner) {
            if (it == true) {
                context?.getColor(R.color.color_orange_app)?.let { it1 -> binding.post.setTextColor(it1) }
                binding.post.isEnabled = true
            } else {
                context?.getColor(R.color.gray)?.let { color ->setUpTextView(color) }
                binding.post.isEnabled = false
            }
        }
        viewModel.proFile.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it.avtPath).placeholder(R.drawable.avatar)
                .fallback(R.drawable.avatar).into(binding.imgAvatar)
            binding.tvUserName.text = it.name
        }
        (activity as MainActivity).viewmodel.listImage.observe(viewLifecycleOwner) {
            val list = arrayListOf<String>()
            it.forEach { item -> list.add(item.uri) }
            postAdapter.setData(list)
            if (list.size > 0)  context?.getColor(R.color.color_orange_app)?.let {color ->setUpTextView(color) }  else
                context?.getColor(R.color.gray)?.let {color ->setUpTextView(color) }
            binding.rcView.goneIf(list.size == 0)
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