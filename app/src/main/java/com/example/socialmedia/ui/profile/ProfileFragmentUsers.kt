package com.example.socialmedia.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.databinding.FragmentProfileUsersBinding
import com.example.socialmedia.databinding.PopupLayoutBinding
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserFragment
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.imagedetails.ImageDetailsFragment
import com.example.socialmedia.ui.home.comment.CommentFragment
import com.example.socialmedia.ui.profile.follow.FollowViewModel
import com.example.socialmedia.ui.profile.follow.user.FollowUserFragment
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOW
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWERS
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWING
import com.example.socialmedia.ui.utils.Constants.KEY_TYPE
import com.example.socialmedia.ui.utils.Constants.KEY_USERID
import com.example.socialmedia.ui.utils.setTextView
import org.koin.android.ext.android.inject


class ProfileFragmentUsers : BaseFragmentWithBinding<FragmentProfileUsersBinding>(),
    View.OnClickListener {
    private val viewModel: ProfileViewModel by inject()
    private val viewModelFollow: FollowViewModel by inject()
    override fun init() {
        val bundle = arguments ?: return
        viewModel.userId = bundle.getString(KEY_USERID)
        viewModel.getProfile(viewModel.userId)
        viewModel.getListDataPostUser(viewModel.userId)
        binding.SwipeRefreshLayout.setOnRefreshListener {
            viewModel.getListDataPost(viewModel.uRepository.getUid())
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.SwipeRefreshLayout.isRefreshing = it
        }
    }

    override fun initData() {
        val adapter = PostAdapter(viewModelFollow) { typeClick, data ->
            clickView(typeClick, data)
        }
        binding.rcvPost.adapter = adapter
        viewModel.listPostUser.observe(viewLifecycleOwner) { listPost ->
            adapter.submitList(listPost)
            if (listPost.size == 0) visibility(View.VISIBLE) else visibility(View.GONE)
        }
        viewModel.liveDataProfile.observe(viewLifecycleOwner) {
            if (it != null) upDateUi(it)
        }
    }

    private fun visibility(visibility: Int) {
        binding.tvNo.visibility = visibility
        binding.ivNo.visibility = visibility
    }

    private fun clickView(typeClick: TypeClick, data: Any) {
        when (typeClick) {
            TypeClick.TYPE_MORE -> {
                openPopup(data as ImageView)
            }

            TypeClick.TYPE_AVT -> {
                openImageFragment()
            }

            else -> {
                showCommentFragment(data as ItemHome)
            }
        }
    }

    private fun showCommentFragment(data: ItemHome) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_ITEM_HOME, data)
        openFragment(CommentFragment::class.java, bundle, true)
    }

    private fun openImageFragment() {
        val bundle = Bundle()
        bundle.putString("imgUrl", viewModel.liveDataProfile.value?.avtPath)
        openFragment(ImageDetailsFragment::class.java, bundle, true)
    }

    private fun upDateUi(profile: Profile) {
        setUpTextViewFollow(profile)
        Glide.with(requireContext()).load(profile.avtPath)
            .placeholder(R.drawable.aaa)
            .fallback(R.drawable.avatar).into(binding.ivProfile)
        binding.tvBirthday.text = profile.dateOfBirth
        binding.tvNameProfile.text = profile.name
        binding.tvDescription.text = profile.description
        binding.tvDescription.goneIf(profile.description == null)
        binding.tvFollowing.text = setTextView(profile.listFollowing?.size)
        binding.tvFollowers.text = setTextView(profile.listFollowers?.size)
        binding.tvPost.text = setTextView(profile.listPost?.size)
        binding.tvNameUser.text = buildString {
            append(profile.name)
            append("'s posts")
        }
    }

    override fun initAction() {
        binding.apply {
            ivProfile.click { showImageViewAvt() }
            ivOption.click { openPopup(it) }
        }
        binding.tvFollow.setOnClickListener { followUsers() }
        binding.ivBack.setOnClickListener { onBackPressed() }
        binding.tvChat.setOnClickListener { showFragmentChats() }
        binding.lnFollowers.setOnClickListener { showFollowFragment(KEY_FOLLOWERS) }
        binding.lnFollowing.setOnClickListener { showFollowFragment(KEY_FOLLOWING) }
    }


    private fun followUsers() {
        viewModel.liveDataProfile.value?.let {
            viewModel.follow(it)
            it.isFollow = !it.isFollow
            setUpTextViewFollow(it)
        }
    }

    private fun setUpTextViewFollow(profile: Profile) {
        binding.tvFollow.text = if (!profile.isFollow) KEY_FOLLOW else KEY_FOLLOWING
        binding.tvFollow.setBackgroundResource(if (!profile.isFollow) R.drawable.bg_following1 else R.drawable.bg_following)
        binding.tvFollow.setTextColor(if (!profile.isFollow) Color.WHITE else Color.BLACK)
    }

    private fun showFollowFragment(type: String) {
        viewModel.liveDataProfile.value?.let {
            val bundle = Bundle()
            bundle.putString(KEY_USERID, it.id)
            bundle.putString(KEY_TYPE, type)
            openFragment(FollowUserFragment::class.java, bundle, true)
        }
    }

    private fun showFragmentChats() {
        viewModel.liveDataProfile.value?.let {
            val bundle = Bundle()
            bundle.putString("id", it.id)
            openFragment(ChatWithUserFragment::class.java, bundle, true)
        }
    }


    private fun showImageViewAvt() {
        viewModel.liveDataProfile.value?.let {
            val bundle = Bundle()
            bundle.putString("imgUrl", it.avtPath)
            openFragment(ImageDetailsFragment::class.java, bundle, true)
        }
    }

    private fun openPopup(view: View) {
        val bindingPopupMenu = PopupLayoutBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            bindingPopupMenu.root, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.showAsDropDown(view, 0, 0)
        bindingPopupMenu.tbBlock.setOnClickListener {
            popupWindow.dismiss()
            openBlockDialog()
        }
        bindingPopupMenu.tbShare.setOnClickListener {
            popupWindow.dismiss()
            toast("This feature is currently under development and will be released soon.")
        }
    }

    private fun openBlockDialog() {
        val bottomBlockFrg = BottomBlockFrg { onClick(it) }
        viewModel.liveDataProfile.value?.let {
            bottomBlockFrg.setData(it)
            bottomBlockFrg.show(childFragmentManager, ProfileFragment::class.java.name)
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_block) {
            viewModel.liveDataProfile.value?.let {
                it.isFollow = false
                viewModel.blockUsers(it)
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentProfileUsersBinding {
        return FragmentProfileUsersBinding.inflate(layoutInflater)
    }
}


