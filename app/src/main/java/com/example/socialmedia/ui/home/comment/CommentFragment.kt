package com.example.socialmedia.ui.home.comment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.databinding.FragmentCommentBinding
import com.example.socialmedia.databinding.PopupLayoutMoreBinding
import com.example.socialmedia.model.Comment
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.ui.home.comment.commentdetail.CommentDetailFragment
import com.example.socialmedia.model.Profile
import com.example.socialmedia.ui.profile.CustomDialog
import com.example.socialmedia.ui.profile.ProfileFragment
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.Constants.KEY_ITEM_HOME
import com.example.socialmedia.ui.utils.Constants.KEY_TYPE
import com.example.socialmedia.ui.utils.getLastTimeAgo
import com.example.socialmedia.ui.utils.setTextView
import com.facebook.AccessTokenManager.Companion.TAG
import org.koin.android.ext.android.inject


class CommentFragment : BaseFragmentWithBinding<FragmentCommentBinding>(), View.OnClickListener {
    private val viewModel: CommentViewmodel by inject()
    private var adapter: CommentAdapter? = null
    var postId: String? = null

    @SuppressLint("SetTextI18n")
    override fun init() {
        val bundle = arguments ?: return
        viewModel.itemHome = bundle.getSerializable(KEY_ITEM_HOME) as ItemHome
        postId = arguments?.getString("Comment")
        initDataPost()
        viewModel.getProfileUserLiveData(viewModel.itemHome.userid)
        viewModel.getProfileLogin()
        initAdapterImageView()
        initAdapterComment()
    }

    private fun initDataPost() {
        viewModel.getDataItemHome()
        viewModel.liveDataItemHome.observe(viewLifecycleOwner) {
            binding.tvContent.text = viewModel.itemHome.content
            binding.tvTime.text = viewModel.itemHome.datetime?.let { getLastTimeAgo(it) }
            binding.tvHeart.text = setTextView(viewModel.itemHome.listUserLike.size)
            binding.tvComment.text = setTextView(viewModel.itemHome.listCommnent.size)
            binding.ivHeart.setImageResource(if (viewModel.isLiked()) R.drawable.ic_heart_true else R.drawable.ic_heart2)
        }
        viewModel.liveDataProfile.observe(viewLifecycleOwner) {
            updateProfileComment(it)
        }
    }

    private fun updateProfileComment(profile: Profile) {
        binding.tvName.text = profile.name
        binding.tvTitle.text = buildString {
            append(profile.name)
            append(" post")
        }
        activity?.let {
            Glide.with(it).load(profile.avtPath)
                .placeholder(R.drawable.avatar)
                .fallback(R.drawable.avatar).into(binding.ivAvt)
            Glide.with(it).load(profile.avtPath)
                .placeholder(R.drawable.avatar)
                .fallback(R.drawable.avatar).into(binding.ivAvtComment)
        }
        if (profile.id?.equals(viewModel.uRepository.getUid()) == true) binding.ivMore.visible() else binding.ivMore.gone()
    }

    private fun initAdapterImageView() {
        if (viewModel.itemHome?.urlImage != null && viewModel.itemHome?.urlImage?.isEmpty() == false) {
            val listImageView = viewModel.itemHome?.urlImage?.toMutableList() as ArrayList<String>
            val adapterView = ImageViewAdapter {
                openFragmentDetail(viewModel.itemHome, it)
            }
            adapterView.setData(listImageView)
            binding.rcvImageview.adapter = adapterView
        }
    }

    private fun openFragmentDetail(itemHome: ItemHome?, urlImageView: String) {
        if (itemHome == null) return
        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_HOME, itemHome)
        bundle.putString("url", urlImageView)
        openFragment(CommentDetailFragment::class.java, bundle, true)
    }

    private fun initAdapterComment() {
        adapter = CommentAdapter(viewModel = viewModel) {
            onClick(it)
        }
        adapter?.setData(viewModel.itemHome?.listCommnent)
        binding.rcvComment.adapter = adapter
    }

    override fun initData() {
        viewModel.getDataItemHome()
        viewModel.liveDataItemHome.observe(viewLifecycleOwner) {
            adapter?.setData(it?.listCommnent)
            binding.tvComment.text = setTextView(it?.listCommnent?.size)
        }
        viewModel.profileLogin.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it?.avtPath).placeholder(R.drawable.avatar)
                .fallback(R.drawable.avatar).into(binding.ivAvtComment)
        }
    }

    override fun initAction() {
        binding.apply {
            ivBack.click { onBackPressed() }
            ivAvt.click { showProfileFrgUser() }
            tvName.click { showProfileFrgUser() }
            ivComment.click { comment() }
            ivSend.click { postComment() }
            ivMore.click { showPopup() }
        }
        binding.ivHeart.setOnClickListener { updateLikePost() }
    }

    private fun updateLikePost() {
        viewModel.updatePostLike {  }
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
                viewModel.deletePost {
                    hideLoadingDialog()
                    if (it) {
                        toast("File deleted successfully.")
                        viewModel.getDataItemHome()
                        onBackPressed()
                    } else {
                        toast("Failed to delete file.")
                    }
                }
            }
        }
        dialog.show()
    }

    private fun comment() {
        val textComment = removeFirstSubstring(
            binding.edtComment.text.toString(),
            "@${viewModel.itemProfile?.name}"
        )
        if (textComment.isNotEmpty()) binding.edtComment.setText(textComment)
        openKeyboard(true)
    }

    private fun removeFirstSubstring(parentStr: String, subStr: String): String {
        return parentStr.replaceFirst(subStr, "")
    }

    private fun openKeyboard(boolean: Boolean) {
        binding.nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
        binding.edtComment.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (boolean) imm.showSoftInput(binding.edtComment, 0) else
            imm.hideSoftInputFromWindow(binding.edtComment.windowToken, 0)
    }


    private fun postComment() {
        val tvComment = binding.edtComment.text?.toString()?.trim()
        if (tvComment.isNullOrEmpty()) return
        viewModel.postComment(tvComment) { comment ->
            if (comment == null) {
                toast("Something went wrong while posting your comment. Please try again later.")
            } else {
                if (!viewModel.itemHome.userid.equals(comment.userId)) viewModel.createNotification(
                    viewModel.itemHome.userid,
                    "Has comment you",
                    "comment" ,  viewModel.itemHome.key
                )
            }
        }
        binding.edtComment.text?.clear()
        openKeyboard(false)
    }


    private fun showProfileFrgUser() {
        val userId = viewModel.uRepository.getUid()
        if (viewModel.itemHome.userid == userId) {
            val bundle = Bundle()
            bundle.putString(KEY_TYPE,KEY_TYPE )
            openFragment(ProfileFragment::class.java, bundle, true)
        } else {
            if (viewModel.itemProfile == null) return
            val bundle = Bundle()
            bundle.putString(Constants.KEY_USERID, viewModel.itemProfile?.id.toString())
            openFragment(ProfileFragmentUsers::class.java, bundle, true)
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentCommentBinding {
        return FragmentCommentBinding.inflate(layoutInflater)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_heart -> updateCommentLike()
            R.id.iv_comment -> updateComment()
            R.id.iv_more_comment -> openPopupComment(v)
        }
    }

    private fun openPopupComment(view: View) {
        val bindingPopupMenu = PopupLayoutMoreBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            bindingPopupMenu.root, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.showAsDropDown(view, 0, 0)
        bindingPopupMenu.tbDelete.setOnClickListener {
            popupWindow.dismiss()
            openDialog(view.tag as Comment)
        }
        bindingPopupMenu.tbEdit.setOnClickListener {
            popupWindow.dismiss()
            toast("Tính năng đang phát triển")
        }
    }
    private fun  openDialog(comment: Comment){
        val dialog =    CustomDialog(requireContext(),"Delete" , "Are you sure you want to delete?", "Cancel" , "Delete" ){
            if(it == "KEY_OK") {
                Log.d(TAG , "CHANNNNNNNNNNNNNNNNNNNNNNNNNNNNN1 ${viewModel.liveDataItemHome.value?.listCommnent?.size}")
                viewModel.liveDataItemHome.value?.listCommnent?.remove(comment.key , comment)
                Log.d(TAG , "CHANNNNNNNNNNNNNNNNNNNNNNNNNNNNN2 ${viewModel.liveDataItemHome.value?.listCommnent?.size}")
//                showLoadingDialog()
                viewModel.deleteComment(comment) {
//                    hideLoadingDialog()
                }
            }
        }
        dialog.show()
    }
    private fun updateComment() {
        toast("Tính năng đang phát triển")
    }

    private fun updateCommentLike() {
        toast("Tính năng đang phát triển")
    }
}