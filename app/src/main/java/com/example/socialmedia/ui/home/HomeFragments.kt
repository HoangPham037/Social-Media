package com.example.socialmedia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.FragmentHomeBinding
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.model.ProfileSigleton
import com.example.socialmedia.ui.chat.chatscreen.ChatFragment
import com.example.socialmedia.ui.home.comment.CommentFragment
import com.example.socialmedia.ui.mainfragment.MainFragment
import com.example.socialmedia.ui.profile.ProfileFragment
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.KEY_ITEM_HOME
import org.koin.android.ext.android.inject


class HomeFragment : BaseFragmentWithBinding<FragmentHomeBinding>() {
    private lateinit var homeAdapter: HomeAdapter

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by inject()

    override fun getViewBinding(inflater: LayoutInflater): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater)
    }

    override fun init() {
        homeAdapter = HomeAdapter(viewModel) { type  , data->
            when (type) {
                TypeClick.TYPE_AVT -> {
                   showProfileFragment(data)
                }
                else -> {
                    showCommentFragment(data)
                }
            }
        }
        binding.rcvView.adapter = homeAdapter
    }

    private fun showProfileFragment(data: ItemHome) {
        val bundle = Bundle()
        data.userid?.let {id ->
            if (id == viewModel.repository.getUid()){
                bundle.putString(Constants.KEY_TYPE , Constants.KEY_TYPE)
                openFragment(ProfileFragment::class.java , bundle , true)
            }else{
                bundle.putString(Constants.KEY_USERID ,id)
                openFragment(ProfileFragmentUsers::class.java , bundle , true)
            }
        }
    }

    private fun showCommentFragment(itemHome: ItemHome) {
        if (view == null) return
        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_HOME, itemHome)
        openFragment(CommentFragment::class.java, bundle, true)
    }

    override fun initData() {
        viewModel.listItemHomeLiveData.observe(viewLifecycleOwner) {
            homeAdapter.submitList(it)
        }
        viewModel.getDataHome()
        viewModel.isLoading.observe(viewLifecycleOwner){
            binding.swiperefresh.isRefreshing = it
        }
        viewModel.getUserData(viewModel.repository.getUid()!!)
        viewModel.userData.observe(this) {
            binding.txtDiamond.text = it.gold.toString()
        }
    }
    override fun initAction() {
        binding.imgMessageScreen.click {
            openFragment(ChatFragment::class.java, null, true)
        }
        binding.imgAvatar.click {
            MainFragment.mainFragment?.openDrawer()
        }

        binding.rcvView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled( recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
                val firstCompletelyVisibleItemPosition =
                    layoutManager!!.findFirstCompletelyVisibleItemPosition()
                if (!binding.swiperefresh.isRefreshing)
                  binding.swiperefresh.isEnabled =  firstVisibleItemPosition <= 0 && firstCompletelyVisibleItemPosition >= 0
            }
        })
        binding.swiperefresh.setOnRefreshListener{
            viewModel.getDataHome()
        }
    }
}