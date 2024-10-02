package com.example.socialmedia.ui.chat.chatscreen.chatwithuser.viewprofile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.showToast
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.common.State
import com.example.socialmedia.model.ActiveStatus
import com.example.socialmedia.databinding.CustomDialogAddUserToBlockBinding
import com.example.socialmedia.databinding.CustomDialogDeleteConversionBinding
import com.example.socialmedia.databinding.CustomDialogRemoveUserToBlockBinding
import com.example.socialmedia.databinding.FragmentViewProfileBinding
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserFragment
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserViewModel
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.utils.Constants
import org.koin.android.ext.android.inject

class ViewProfileFragment : BaseFragmentWithBinding<FragmentViewProfileBinding>() {
    private val chatWithUserViewModel: ChatWithUserViewModel by inject()
    private val viewProfileViewModel: ViewProfileViewModel by inject()
    private var conversionId: String? = null

    override fun getViewBinding(inflater: LayoutInflater): FragmentViewProfileBinding {
        return FragmentViewProfileBinding.inflate(layoutInflater)
    }

    override fun init() {
        val receiverId = arguments?.getString("createNotification")
        /** do nothing **/
    }

    override fun initData() {
        setupViewDetails()
    }

    private fun setupViewDetails() {
        val receiverId = arguments?.getString(Constants.KEY_RECEIVER_ID)
        conversionId = arguments?.getString(Constants.KEY_CONVERSION_ID)
        if (receiverId != null) {
            chatWithUserViewModel.userInfo.observe(viewLifecycleOwner) { userSender ->
                val isBlock = userSender.listBlock?.any {
                    it == receiverId
                }
                if (isBlock == true) {
                    binding.imgBlock.setImageResource(R.drawable.ic_un_block)
                } else {
                    binding.imgBlock.setImageResource(R.drawable.ic_block_chat)
                }
                binding.imgBlock.click {
                    if (isBlock == true) {
                        val title = resources.getString(R.string.text_un_block_user)
                        val description =
                            resources.getString(R.string.text_des_un_block_user)
                        showCommonDialog(
                            R.layout.custom_dialog_remove_user_to_block,
                            title,
                            description
                        ) {
                            viewProfileViewModel.removeUserToBlock(receiverId)
                        }
                    } else {
                        val title = resources.getString(R.string.text_block_user)
                        val description = resources.getString(R.string.text_des_block_user)

                        showCommonDialog(
                            R.layout.custom_dialog_add_user_to_block,
                            title,
                            description
                        ) {
                            viewProfileViewModel.addUserToBlock(receiverId)
                        }
                    }
                }
            }
            chatWithUserViewModel.getUserByUserId(Constants.KEY_COLLECTION_USER)


            chatWithUserViewModel.otherUserInfo.observe(viewLifecycleOwner) { userInfo ->
                Glide.with(requireActivity()).load(userInfo.avtPath).placeholder(R.drawable.avatar).fallback(R.drawable.avatar).into(binding.imgAvatar)
                binding.tvNameReceiver.text = userInfo.name
                binding.tvName.text = userInfo.name
                if (userInfo.description.isNullOrEmpty()) {
                    binding.layoutDescription.gone()
                    binding.view.gone()
                }else {
                    binding.layoutDescription.visible()
                    binding.view.visible()
                }
                binding.tvDescription.text = userInfo.description
                binding.tvBirthday.text = userInfo.dateOfBirth
                if (userInfo.status == ActiveStatus.Offline.name) {
                    binding.imgActiveState.gone()
                } else {
                    binding.imgActiveState.visible()
                }
                binding.imgDetailsProfile.click {
                    userInfo.id?.let { uid -> showProfile(uid) }
                }
            }
            chatWithUserViewModel.getOtherUserById(receiverId, Constants.KEY_COLLECTION_USER)
        }
    }

    private fun showProfile(receiverId: String) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_USERID, receiverId)
        openFragment(ProfileFragmentUsers::class.java, bundle, true)
    }

    override fun initAction() {
        binding.imgBack.click {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.imgDeleteChat.click {
            conversionId?.let { conversionIds ->
                val title = resources.getString(R.string.text_delete_conversion)
                val description = resources.getString(R.string.text_des_delete_conversion)
                showCommonDialog(R.layout.custom_dialog_delete_conversion, title, description) {
                    chatWithUserViewModel.deleteConversion(
                        conversionIds
                    ) {
                        when (it) {
                            is State.Loading -> showLoadingDialog()
                            is State.Success -> hideLoadingDialog()
                            else -> requireActivity().showToast(it.toString())
                        }
                    }
                    chatWithUserViewModel.deleteConversionMessage(
                        conversionIds
                    ) {
                        when (it) {
                            is State.Loading -> showLoadingDialog()
                            is State.Success -> hideLoadingDialog()
                            else -> requireActivity().showToast(it.toString())
                        }
                    }
                    activity?.supportFragmentManager?.popBackStack(
                        ChatWithUserFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )

                }
            }
        }
    }

    private fun showCommonDialog(
        layoutId: Int,
        title: String,
        description: String,
        onConfirmClick: () -> Unit,
    ) {
        val dialog = Dialog(requireActivity())
        val bindingDialog: ViewDataBinding = when (layoutId) {
            R.layout.custom_dialog_delete_conversion -> CustomDialogDeleteConversionBinding.inflate(
                layoutInflater
            )

            R.layout.custom_dialog_add_user_to_block -> CustomDialogAddUserToBlockBinding.inflate(
                layoutInflater
            )

            R.layout.custom_dialog_remove_user_to_block -> CustomDialogRemoveUserToBlockBinding.inflate(
                layoutInflater
            )

            else -> throw IllegalArgumentException("Invalid layout id")
        }
        dialog.setContentView(bindingDialog.root)
        dialog.window?.setBackgroundDrawable(null)
        dialog.setCancelable(false)
        when (layoutId) {
            R.layout.custom_dialog_delete_conversion -> {
                with((bindingDialog as CustomDialogDeleteConversionBinding)) {
                    tvTitle.text = title
                    tvDescription.text = description
                    tvConfirm.click {
                        onConfirmClick()
                        dialog.dismiss()
                    }
                    tvConfirmCancel.click { dialog.dismiss() }
                }
            }

            R.layout.custom_dialog_add_user_to_block -> {
                with((bindingDialog as CustomDialogAddUserToBlockBinding)) {
                    tvTitle.text = title
                    tvDescription.text = description
                    tvConfirm.click {
                        onConfirmClick()
                        dialog.dismiss()
                    }
                    tvConfirmCancel.click { dialog.dismiss() }
                }
            }

            R.layout.custom_dialog_remove_user_to_block -> {
                with((bindingDialog as CustomDialogRemoveUserToBlockBinding)) {
                    tvTitle.text = title
                    tvDescription.text = description
                    tvConfirm.click {
                        onConfirmClick()
                        dialog.dismiss()
                    }
                    tvConfirmCancel.click { dialog.dismiss() }
                }
            }
        }
        dialog.show()
    }
}