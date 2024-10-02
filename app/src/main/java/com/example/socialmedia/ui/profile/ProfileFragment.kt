package com.example.socialmedia.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.PermissionFragment
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.databinding.FragmentProfileBinding
import com.example.socialmedia.databinding.PopupLayoutMoreBinding
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.imagedetails.ImageDetailsFragment
import com.example.socialmedia.ui.home.comment.CommentFragment
import com.example.socialmedia.ui.home.post.EditPostFragment
import com.example.socialmedia.ui.home.post.PostFragment
import com.example.socialmedia.ui.profile.account.AccountFragment
import com.example.socialmedia.ui.profile.account.PersonalFragment
import com.example.socialmedia.ui.profile.follow.FollowFragment
import com.example.socialmedia.ui.profile.follow.FollowViewModel
import com.example.socialmedia.ui.setting.SettingFragment
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWERS
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWING
import com.example.socialmedia.ui.utils.Constants.KEY_ITEM_HOME
import com.example.socialmedia.ui.utils.Constants.KEY_TYPE
import com.example.socialmedia.ui.utils.Constants.KEY_USERID
import com.example.socialmedia.ui.utils.setTextView
import org.koin.android.ext.android.inject


class ProfileFragment : PermissionFragment<FragmentProfileBinding>(), View.OnClickListener {
    private val viewModel: FollowViewModel by inject()
    private var dialog: BottomAvatarFrg? = null
    private var adapter: PostAdapter? = null

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun init() {
        adapter = PostAdapter(viewModel) { type, data ->
            clickView(type, data)
        }
        binding.rcvView.adapter = adapter
        viewModel.getProfile(viewModel.uRepository.getUid())
        viewModel.liveDataProfile.observe(viewLifecycleOwner) { profile ->
            updateProfile(profile)
        }
        binding.rcvView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                layoutManager?.let {
                    val firstVisibleItemPosition = it.findFirstVisibleItemPosition()
                    val firstCompletelyVisibleItemPosition =
                        it.findFirstCompletelyVisibleItemPosition()
                    if (!binding.SwipeRefreshLayout.isRefreshing)
                        binding.SwipeRefreshLayout.isEnabled =
                            firstVisibleItemPosition <= 0 && firstCompletelyVisibleItemPosition >= 0
                }
            }
        })
        binding.SwipeRefreshLayout.setOnRefreshListener {
            viewModel.getListDataPost(viewModel.uRepository.getUid())
            viewModel.isLoading.value?.let {
                binding.SwipeRefreshLayout.isRefreshing = it
            }
        }
        val bundle = arguments ?: return
        val type = bundle.getString(KEY_TYPE)
        binding.ivBack.visibility = if (type != null) View.VISIBLE else View.GONE
    }

    private fun updateProfile(profile: Profile) {
        binding.tvNameProfile.text = profile.name
        binding.tvBirthday.text = profile.dateOfBirth
        if (profile.description == null || profile.description?.isEmpty() == true) upDateUi(
            View.GONE,
            View.VISIBLE
        ) else upDateUi(View.VISIBLE, View.GONE)
        binding.tvDescription.text = profile.description
        binding.tvNameProfile.goneIf(profile.name == null)
        binding.tvFollowing.text = setTextView(profile.listFollowing?.size)
        binding.tvFollowers.text = setTextView(profile.listFollowers?.size)
        Glide.with(binding.root.context).load(profile.avtPath)
            .placeholder(R.drawable.avatar)
            .fallback(R.drawable.avatar).into(binding.ivProfile)
        viewModel.uRepository.getUid()?.let { it1 -> viewModel.getListDataPost(it1) }
        val boolean = profile.listPost?.isEmpty() == true || profile.listPost == null
        if (boolean) visibility(View.VISIBLE) else visibility(View.GONE)
    }

    private fun upDateUi(gone: Int, visible: Int) {
        binding.tvDescription.visibility = gone
        binding.tbAdd.visibility = visible
    }

    private fun clickView(typeClick: TypeClick, data: Any) {
        when (typeClick) {
            TypeClick.TYPE_MORE -> {
                openPopup(data as ImageView)
            }

            else -> {
                showCommentFragment(data as ItemHome)
            }
        }
    }

    override fun initData() {
        dialog = BottomAvatarFrg { onClick(it) }
        viewModel.getListPost.observe(viewLifecycleOwner) { listPost ->
            binding.SwipeRefreshLayout.isRefreshing = false
            adapter?.submitList(listPost)
        }
        viewModel.getListDataPost(viewModel.uRepository.getUid())
    }

    private fun visibility(visibility: Int) {
        binding.tvNo.visibility = visibility
        binding.ivNo.visibility = visibility
    }

    private fun openPopup(view: ImageView) {
        val bindingPopupMenu = PopupLayoutMoreBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            bindingPopupMenu.root, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.showAsDropDown(view, 0, 0)
        bindingPopupMenu.tbDelete.setOnClickListener {
            popupWindow.dismiss()
            openDialog(view.tag as ItemHome)
        }
        bindingPopupMenu.tbEdit.setOnClickListener {
            popupWindow.dismiss()
            val bundle = Bundle()
            bundle.putSerializable(KEY_ITEM_HOME , view.tag as ItemHome)
            openFragment(EditPostFragment::class.java , bundle , true)
        }
    }

    private fun openDialog(data: ItemHome) {
        val dialog = CustomDialog(
            requireContext(),
            "Delete",
            "Are you sure you want to delete?",
            "Cancel",
            "Delete"
        ) {
            if (it == "KEY_OK") {
                showLoadingDialog()
                viewModel.deletePost(data) {boolean ->
                    hideLoadingDialog()
                    if (boolean) {
                        toast("File deleted successfully.")
                    } else {
                        toast("Failed to delete file.")
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showCommentFragment(data: ItemHome) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_ITEM_HOME, data)
        openFragment(CommentFragment::class.java, bundle, true)
    }

    override fun initAction() {
        binding.apply {
            ivSetting.click { showFrgSetting() }
            ivProfile.click { openSheetDialog() }
            lnFollowers.click { showListFollow(KEY_FOLLOWERS) }
            lnFollowing.click { showListFollow(KEY_FOLLOWING) }
            ivBack.click { onBackPressed() }
            tvEditProfile.click { editProfile() }
            tbAdd.click { openFragmentPersonal() }
        }
    }

    private fun openFragmentPersonal() {
        openFragment(PersonalFragment::class.java, null, true)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tb_change_avt -> openChoosePictureFrg()
            R.id.tb_view_avt -> showImageViewAvt()
        }
    }

    private fun openChoosePictureFrg() {
        openFragment(ChoosePictureFrg::class.java, null, true)
    }

    private fun showImageViewAvt() {
        val bundle = Bundle()
        bundle.putString("imgUrl", viewModel.liveDataProfile.value?.avtPath)
        openFragment(ImageDetailsFragment::class.java, bundle, true)
    }

    private fun openSheetDialog() {
        dialog?.show(childFragmentManager, ProfileFragment::class.java.name)
    }

    private fun showFrgSetting() {
        openFragment(SettingFragment::class.java, null, true)
    }

    private fun editProfile() {
        openFragment(AccountFragment::class.java, null, true)
    }

    private fun showListFollow(type: String) {
        if (viewModel.liveDataProfile.value == null) return
        val bundle = Bundle()
        bundle.putString(KEY_USERID, viewModel.liveDataProfile.value?.id)
        bundle.putString(KEY_TYPE, type)
        openFragment(FollowFragment::class.java, bundle, true)
    }
}

