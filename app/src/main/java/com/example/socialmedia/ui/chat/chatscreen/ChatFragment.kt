package com.example.socialmedia.ui.chat.chatscreen

import android.app.Dialog
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.showToast
import com.example.socialmedia.base.utils.showToasts
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.CustomDialogDeleteConversionBinding
import com.example.socialmedia.databinding.FragmentChatBinding
import com.example.socialmedia.model.MuteNotification
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.chat.chatscreen.newsinglechat.NewChatFragment
import com.example.socialmedia.ui.chat.chatscreen.searchconversion.SearchConversionFragment
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.CustomToast
import org.koin.android.ext.android.inject

class ChatFragment : BaseFragmentWithBinding<FragmentChatBinding>() {
    private lateinit var recentConversionAdapter: ChatAdapter
    private val mainViewModel: MainViewModel by inject()
    private val chatViewModel: ChatViewModel by inject()
    private var dialog: BottomSheetOption? = null

    companion object {
        private var instance: ChatFragment? = null
        fun newInstance(): ChatFragment {
            if (instance == null) {
                instance = ChatFragment()
            }
            return instance!!
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentChatBinding {
        return FragmentChatBinding.inflate(layoutInflater)
    }

    override fun init() {
        setupConversionRecyclerview()
    }

    override fun initData() {
        mainViewModel.getUserById(Constants.KEY_COLLECTION_USER) { userInfo ->
            context?.let {
                Glide.with(it).load(userInfo.avtPath).placeholder(R.drawable.avatar)
                    .fallback(R.drawable.avatar).into(binding.imgAvatar)
            }
        }
        listenerConversion()
    }

    private fun listenerConversion() {
        chatViewModel.listConversion.observe(viewLifecycleOwner) { listConversion ->
            listConversion?.let {
                if (it.isNotEmpty()) {
                    binding.rcConversion.visible()
                    recentConversionAdapter.submitList(listConversion)
                    binding.rcConversion.smoothScrollToPosition(0)
                } else {
                    binding.layoutEmpty.visible()
                    binding.rcConversion.gone()
                }
            }
        }
        chatViewModel.getAllChatroomCollectionReference()
    }

    override fun initAction() {
        binding.imgAddNewChat.click {
            openFragment(NewChatFragment::class.java, null, true)
        }
        binding.imgSearch.click {
            openFragment(SearchConversionFragment::class.java, null, true)
        }
    }

    private fun setupConversionRecyclerview() {
        recentConversionAdapter =
            ChatAdapter(mainViewModel.getUid()!!, requireContext(), chatViewModel) { conversions ->

                dialog = BottomSheetOption(
                    mainViewModel.getUid()!!,
                    conversions
                ) { type, conversion, typeLayoutClick ->
                    val uidNeedMute = conversion.split("_")[1]
                    val muteNotification = MuteNotification(uid = uidNeedMute)
                    when (typeLayoutClick) {
                        TypeLayoutClick.MARK_AS_UNREAD -> {
                            markAsUnreadConversion(conversion)
                            dialog?.dismiss()
                        }

                        TypeLayoutClick.MUTE -> {
                            if (type == "send") {
                                chatViewModel.addToListUidNeedMute(muteNotification)
                                conversions.id?.let {
                                    chatViewModel.updateIsMute(
                                        true,
                                        it,
                                        Constants.KEY_IS_MUTE_SEND
                                    )
                                }
                                dialog?.dismiss()
                            } else if (type == "receiver") {
                                chatViewModel.addToListUidNeedMute(muteNotification)
                                conversions.id?.let {
                                    chatViewModel.updateIsMute(
                                        true,
                                        it,
                                        Constants.KEY_IS_MUTE_RECEIVER
                                    )
                                }
                                dialog?.dismiss()
                            }
                        }

                        TypeLayoutClick.UN_MUTE -> {
                            if (type == "send") {
                                chatViewModel.removeUidInListNeedMute(muteNotification)
                                conversions.id?.let {
                                    chatViewModel.updateIsMute(
                                        false,
                                        it,
                                        Constants.KEY_IS_MUTE_SEND
                                    )
                                }
                                dialog?.dismiss()
                            } else if (type == "receiver") {
                                chatViewModel.removeUidInListNeedMute(muteNotification)
                                conversions.id?.let {
                                    chatViewModel.updateIsMute(
                                        false,
                                        it,
                                        Constants.KEY_IS_MUTE_RECEIVER
                                    )
                                }
                                dialog?.dismiss()
                            }

                        }

                        TypeLayoutClick.DELETE_CONVERSION -> {
                            showDialogConfirmDelete(conversion)
                            dialog?.dismiss()
                        }
                    }
                }
                dialog?.show(childFragmentManager, ChatFragment::class.java.name)
            }
        binding.rcConversion.setHasFixedSize(true)
        binding.rcConversion.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcConversion.adapter = recentConversionAdapter
    }

    private fun markAsUnreadConversion(conversionId: String) {
        chatViewModel.updateStateSeen(false, conversionId)
    }

    private fun showDialogConfirmDelete(conversionId: String) {
        val dialogConfirm = Dialog(requireContext())
        val dialogBinding = CustomDialogDeleteConversionBinding.inflate(layoutInflater)
        dialogConfirm.setContentView(dialogBinding.root)
        dialogConfirm.window?.setBackgroundDrawable(null)
        dialogConfirm.setCancelable(false)
        val title = resources.getString(R.string.text_delete_conversion)
        val description = resources.getString(R.string.text_des_delete_conversion)
        dialogBinding.tvTitle.text = title
        dialogBinding.tvDescription.text = description
        dialogBinding.tvConfirmCancel.click { dialogConfirm.dismiss() }
        dialogBinding.tvConfirm.click {
            deleteConversion(conversionId)
            dialogConfirm.dismiss()
        }
        dialogConfirm.show()
    }

    private fun deleteConversion(conversionId: String) {
        chatViewModel.deleteConversion(
            conversionId
        ) {
            when (it) {
                is State.Loading -> showLoadingDialog()
                is State.Success -> hideLoadingDialog()
                else -> requireActivity().showToast(it.toString())
            }
        }
        chatViewModel.deleteConversionMessage(
            conversionId
        ) {
            when (it) {
                is State.Loading -> showLoadingDialog()
                is State.Success -> hideLoadingDialog()
                else -> requireActivity().showToast(it.toString())
            }
        }
    }

}