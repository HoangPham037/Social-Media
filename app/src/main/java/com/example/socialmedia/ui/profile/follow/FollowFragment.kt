package com.example.socialmedia.ui.profile.follow

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.DialogDeleteBinding
import com.example.socialmedia.databinding.FragmentFollowBinding
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.INDEX_0
import com.example.socialmedia.ui.utils.Constants.INDEX_1
import com.example.socialmedia.ui.utils.Constants.KEY_COLLECTION_USER
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWERS
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWING
import com.example.socialmedia.ui.utils.Constants.KEY_TYPE
import com.example.socialmedia.ui.utils.Constants.KEY_USERID
import com.google.android.material.tabs.TabLayout
import com.google.common.eventbus.Subscribe
import org.koin.android.ext.android.inject
import java.util.ArrayList


class FollowFragment : BaseFragmentWithBinding<FragmentFollowBinding>() {
    private var adapter: AdapterFollow? = null
    private val viewModel: FollowViewModel by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentFollowBinding {
        return FragmentFollowBinding.inflate(layoutInflater)
    }

    @Subscribe
    override fun init() {
        val bundle = arguments ?: return
        viewModel.type = bundle.getString(KEY_TYPE)
        getDataProfile(viewModel.uRepository.getUid())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        binding.SwipeRefreshLayout.setOnRefreshListener {
            getDataProfile(viewModel.uRepository.getUid())
        }
        viewModel.listFollowersLiveData.observe(viewLifecycleOwner) {
            if (viewModel.type?.equals(KEY_FOLLOWERS) == true) showListFollow(INDEX_0)
        }
        viewModel.listFollowingLiveData.observe(viewLifecycleOwner) {
            if (viewModel.type?.equals(KEY_FOLLOWING) == true) showListFollow(INDEX_1)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.SwipeRefreshLayout.isRefreshing = it
        }
        adapter = AdapterFollow(viewModel) {
            if (it.tag != null) {
                when (it.id) {
                    R.id.tv_follow -> follow(it.tag as Profile)
                    R.id.iv_menu_follow -> openDialogDelete(it.tag as Profile)
                    R.id.item_follow -> showProfile(it.tag as Profile)
                }
            }
        }
        binding.rcvFollow.adapter = adapter
    }

    private fun getDataProfile(uid: String?) {
        viewModel.getProfileUser(uid) {
            viewModel.isLoading.postValue(false)
            when (it) {
                is State.Success -> {
                    viewModel.isLoading.postValue(false)
                    it.data.let { profile ->
                        getListFollowing(profile)
                        getListFollowers(profile)
                        viewModel.profile = profile
                        binding.tvName.text = profile.name
                    }
                }

                else -> {
                    Log.d(ContentValues.TAG, "bind: lỗi ")
                }
            }
        }
    }

    private fun visibility(visibility: Int) {
        binding.tvNo.visibility = visibility
        binding.ivNo.visibility = visibility
    }

    private fun getListFollowers(profile: Profile) {
        if (profile.listFollowers == null || profile.listFollowers?.isEmpty() == true) {
            showFollow(viewModel.type)
            return
        }
        viewModel.getListFollowers(profile)
    }

    private fun getListFollowing(profile: Profile) {
        if (profile.listFollowing == null || profile.listFollowing?.isEmpty() == true) {
            hideLoadingDialog()
            showFollow(viewModel.type)
            return
        }
        viewModel.getListFollowing(profile)
    }


    private fun showFollow(type: String?) {
        showListFollow(if (type == KEY_FOLLOWERS) INDEX_0 else INDEX_1)
    }

    override fun initAction() {
        binding.tabLayout.setSelectedTabIndicatorColor(Color.BLACK)
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(KEY_FOLLOWERS))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(KEY_FOLLOWING))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showListFollow(tab.position)
                binding.edtSearch.text?.clear()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.apply {
            ivBack.click { onBackPressed() }
            imgClear.click {
                binding.edtSearch.text?.clear()
            }
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val queryText = charSequence.toString().trim()
                binding.imgClear.visibility =
                    if (queryText.isNotEmpty()) View.VISIBLE else View.GONE
                if (queryText.isNotEmpty()) searchUser(queryText) else viewModel.index?.let {
                    showListFollow(it)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchUser(queryText: String) {
        viewModel.index?.let { index ->
            viewModel.profile?.let { profile ->
                viewModel.searchUserByName(
                    index,
                    profile,
                    queryText,
                    Constants.KEY_USER_NAME,
                    KEY_COLLECTION_USER
                )
            }
        }
        viewModel.listUser.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
            if (it.isEmpty() || it.size == 0) visibility(View.VISIBLE) else visibility(View.GONE)
        }
    }

    private fun showListFollow(index: Int) {
        viewModel.index = index
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(index), false)
        when (index) {
            INDEX_0 -> {
                viewModel.type = KEY_FOLLOWERS
                adapter?.submitList(viewModel.listFollowersLiveData.value)
                checkDataFollow(viewModel.listFollowersLiveData.value)
            }
            INDEX_1 -> {
                viewModel.type = KEY_FOLLOWING
                adapter?.submitList(viewModel.listFollowingLiveData.value)
                checkDataFollow(viewModel.listFollowingLiveData.value)
            }
        }
        binding.tabLayout.visibility = View.VISIBLE
    }

    private fun checkDataFollow(value: ArrayList<Profile>?) {
        if (value?.isEmpty() == true || value?.size == 0 || value == null) visibility(View.VISIBLE) else visibility(View.GONE)
    }


    private fun follow(profile: Profile) {
        viewModel.follow(profile)
    }

    private fun showProfile(profile: Profile?) {
        val bundle = Bundle()
        bundle.putString(KEY_USERID, profile?.id)
        openFragment(ProfileFragmentUsers::class.java, bundle, true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun openDialogDelete(profile: Profile) {
        val binding = DialogDeleteBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(null)
        binding.tvCancel.setOnClickListener { dialog.dismiss() }
        binding.tvDelete.setOnClickListener {
            dialog.dismiss()
            showLoadingDialog()
            viewModel.removeUser(profile) {
                hideLoadingDialog()
                if (it) {
                    viewModel.remove(profile)
                    adapter?.notifyDataSetChanged()
                    if (viewModel.listFollowersLiveData.value?.size == 0) visibility(View.VISIBLE)
                } else {
                    toast("The server is currently unavailable. Please try again later.")
                }
            }
        }
        dialog.show()
    }
}




