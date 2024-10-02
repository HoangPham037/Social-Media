package com.example.socialmedia.ui.home.post.gallery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R
import com.example.socialmedia.base.PermissionFragment
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentGalleryImageBinding
import org.koin.android.ext.android.inject

class GalleryImageFragment : PermissionFragment<FragmentGalleryImageBinding>() {

    companion object {
        fun newInstance() = GalleryImageFragment()
    }

    private val viewModel: GalleryImageViewModel by  inject()
    private lateinit var galleryAdapter: GalleryAdapter
    override fun getViewBinding(inflater: LayoutInflater): FragmentGalleryImageBinding {
        return FragmentGalleryImageBinding.inflate(inflater)
    }

    override fun init() {
        galleryAdapter = GalleryAdapter() {
            val boolean = galleryAdapter.listItemSelected.size > 0
            binding.btnNext.isEnabled = boolean
            (if(boolean) context?.getColor(R.color.purple) else  context?.getColor(R.color.gray))?.let {
                binding.btnNext.setTextColor(it)
            }
        }
        binding.rcvView.adapter = galleryAdapter
        binding.rcvView.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        openGallery {
            viewModel.getAllImages()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        viewModel.imageAllGrallery.observe(viewLifecycleOwner) {
            val listImage = it?.toMutableList()?.reversed()
            galleryAdapter.listItemSelected =
                (activity as MainActivity).viewmodel.listImage.value ?: arrayListOf()
            galleryAdapter.submitList(listImage)
            binding.ivEmpty.visibility = if(listImage?.size == 0) View.VISIBLE else View.GONE
            binding.tvEmpty.visibility = if(listImage?.size == 0) View.VISIBLE else View.GONE
        }
    }

    override fun initAction() {
        binding.materialToolbar.click {
            onBackPressed()
        }
        binding.btnNext.click {
            (activity as MainActivity).viewmodel.setListImage(galleryAdapter.listItemSelected)
            onBackPressed()
        }
    }
}