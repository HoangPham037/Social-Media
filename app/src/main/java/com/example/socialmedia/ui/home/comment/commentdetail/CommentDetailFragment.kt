package com.example.socialmedia.ui.home.comment.commentdetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.FragmentCommentDetailBinding
import com.example.socialmedia.databinding.PopupLayoutMoreBinding
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.imagedetails.ImageDetailsFragment
import com.example.socialmedia.ui.home.comment.CommentAdapter
import com.example.socialmedia.ui.home.comment.CommentViewmodel
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.profile.CustomDialog
import com.example.socialmedia.ui.profile.ProfileFragment
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.setTextView
import org.koin.android.ext.android.inject

class CommentDetailFragment : BaseFragmentWithBinding<FragmentCommentDetailBinding>(),
    View.OnClickListener {
    private val viewModel: CommentViewmodel by inject()
    private var adapter: CommentAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun init() {
        val bundle = arguments ?: return
        viewModel.itemHome = bundle.getSerializable(Constants.KEY_ITEM_HOME) as ItemHome
        val url = bundle.getString("url") as String
        viewModel.itemHome?.let {
            viewModel.getProfileUser(it.userid) { profile ->
                updateProfileComment(profile)
            }
            binding.tvContent.text = it.content
            binding.tvComment.text = setTextView(it.listCommnent?.size)
            binding.tvHeart.text = setTextView(it.listUserLike?.size)
            binding.ivHeart.setImageResource(if (it.listUserLike?.contains(viewModel.uRepository.getUid()) == true) R.drawable.ic_heart_true else R.drawable.ic_heart2)
            binding.ivMore.visibility =
                if (it.userid?.equals(viewModel.uRepository.getUid()) == true) View.VISIBLE else View.GONE
            initAdapterComment(it)
            initAdapterImageView(it, url)
        }
    }

    private fun initAdapterImageView(itemHome: ItemHome, url: String) {
        val adapterDetailAdapter = CommentDetailAdapter(viewModel) {
            val bundle = Bundle()
            val uriImage = it.tag as String
            bundle.putString("imgUrl", uriImage)
            openFragment(ImageDetailsFragment::class.java, bundle, true)
        }
        adapterDetailAdapter.submitList(itemHome.urlImage)
        itemHome.urlImage?.indexOf(url)?.let { binding.rcvComment.scrollToPosition(it) }
        binding.rcvComment.adapter = adapterDetailAdapter
    }

    private fun updateProfileComment(profile: State<Profile>) {
        when (profile) {
            is State.Success -> {
                profile.data.also { viewModel.itemProfile = it }
                binding.tvName.text = profile.data.name
                binding.tvTitle.text = buildString {
                    append(profile.data.name)
                    append(" post")
                }
                Glide.with(requireContext()).load(profile.data.avtPath).fallback(R.drawable.aaa)
                    .placeholder(R.drawable.aaa).into(binding.ivAvt)
                Glide.with(requireContext()).load(profile.data.avtPath).fallback(R.drawable.aaa)
                    .placeholder(R.drawable.aaa).into(binding.ivAvtComment)
            }

            else -> {
                binding.tvName.text = buildString {
                    append("Error updating data, please try again.")
                }
            }
        }
    }

    private fun initAdapterComment(itemHome: ItemHome) {
        adapter = CommentAdapter(viewModel = viewModel) {
            onClick(it)
        }
        adapter?.setData(itemHome.listCommnent)
        binding.rcvComment.adapter = adapter
    }

    override fun initData() {

    }

    override fun initAction() {
        binding.apply {
            ivMore.click { showPopup() }
            ivBack.click { onBackPressed() }
            ivComment.click { onBackPressed() }
            ivAvt.click { showProfileFrgUser() }
            tvName.click { showProfileFrgUser() }
            ivHeart.click { upDateLikePost() }
        }
    }

    private fun upDateLikePost() {
        viewModel.updatePostLike { type ->
            when (type) {
                "add" -> {
                    upDateUi()
                }

                "remove" -> {
                    upDateUi()
                }

                else -> {
                    Log.d(ContentValues.TAG, "bind: lỗi ")
                }
            }
        }
    }

    private fun upDateUi() {
        viewModel.itemHome?.let {
            viewModel.getData(it) {
                if (it != null) {
                    viewModel.itemHome = it
                    binding.tvHeart.text = setTextView(it.listUserLike?.size)
                    binding.ivHeart.setImageResource(if (it.listUserLike?.contains(viewModel.uRepository.getUid()) == true) R.drawable.ic_heart_true else R.drawable.ic_heart2)
                }
            }
        }
    }

    private fun showProfileFrgUser() {
        if (viewModel.itemHome?.userid.equals(viewModel.uRepository.getUid())) {
            val bundle = Bundle()
            bundle.putString(Constants.KEY_TYPE, Constants.KEY_TYPE)
            openFragment(ProfileFragment::class.java, bundle, true)
        } else {
            val bundle = Bundle()
            bundle.putString(Constants.KEY_USERID, viewModel.itemProfile?.id)
            openFragment(ProfileFragmentUsers::class.java, bundle, true)
        }
    }

    private fun showPopup() {
        val bindingPopupMenu = PopupLayoutMoreBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            bindingPopupMenu.root, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.showAsDropDown(binding.ivMore, 0, 0)
        bindingPopupMenu.tbDelete.setOnClickListener {
            popupWindow.dismiss()
            openDialog()
        }
        bindingPopupMenu.tbEdit.setOnClickListener {
            popupWindow.dismiss()
            toast("Tính năng đang phát triển")
        }
    }

    private fun openDialog() {
        val dialog =    CustomDialog(requireContext(),"Delete" , "Are you sure you want to delete?", "Cancel" , "Delete" ){
            if(it == "KEY_OK") {
                showLoadingDialog()
                viewModel.itemHome?.let { item ->
                    viewModel.deleteItemHome(item) {
                        hideLoadingDialog()
                        if (it) {
                            toast("File deleted successfully.")
                            onBackPressed()
                        } else {
                            toast("Failed to delete file.")
                        }
                    }
                }
            }
        }
        dialog.show()
    }


    override fun getViewBinding(inflater: LayoutInflater): FragmentCommentDetailBinding {
        return FragmentCommentDetailBinding.inflate(layoutInflater)
    }

    override fun onClick(v: View) {
      toast("CHANN")
    }
}