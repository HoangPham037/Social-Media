package com.example.socialmedia.ui.chat.chatscreen.newsinglechat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.hideKeyboard
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.FragmentNewChatBinding
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserFragment
import com.example.socialmedia.ui.chat.chatscreen.searchconversion.SearchConversionViewModel
import com.example.socialmedia.ui.chat.chatscreen.searchconversion.SearchUserRecyclerAdapter
import com.example.socialmedia.ui.utils.Constants
import org.koin.android.ext.android.inject


class NewChatFragment : BaseFragmentWithBinding<FragmentNewChatBinding>() {
    private lateinit var searchUserRecyclerAdapter: SearchUserRecyclerAdapter
    private val searchViewModels: SearchConversionViewModel by inject()
    private val mainViewModel: MainViewModel by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentNewChatBinding {
        return FragmentNewChatBinding.inflate(layoutInflater)
    }

    override fun init() {
    }

    override fun initData() {
    }

    override fun initAction() {
        binding.icBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.autoSearch.addTextChangedListener(edtChangeListener)
    }

    private val edtChangeListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val queryText = p0.toString().trim()

            searchViewModels.listUser.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is State.Change -> {
                        binding.rcListUserSearch.gone()
                        binding.layoutNoResultFound.gone()
                    }
                    is State.Success -> {
                        if (state.data!!.isNotEmpty()) {
                            binding.rcListUserSearch.visible()
                            binding.layoutNoResultFound.gone()
                            searchUserRecyclerAdapter = SearchUserRecyclerAdapter(
                                mainViewModel.getUid()!!,
                                requireActivity()
                            ) { userInfo ->
                                userInfo.id?.let { uid ->
                                    navigateToChatWithUser(uid)
                                    view?.hideKeyboard(requireActivity())
                                }
                            }
                            binding.rcListUserSearch.setHasFixedSize(true)
                            binding.rcListUserSearch.layoutManager =
                                LinearLayoutManager(
                                    requireActivity(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            binding.rcListUserSearch.adapter = searchUserRecyclerAdapter
                            searchUserRecyclerAdapter.submitList(state.data)
                        } else {
                            binding.layoutNoResultFound.visible()
                            binding.rcListUserSearch.gone()
                        }
                    }

                    else -> {}
                }
            }

            searchViewModels.searchUserByName(
                queryText,
                Constants.KEY_USER_NAME,
                Constants.KEY_COLLECTION_USER
            )
            if (queryText.isNotEmpty()) {
                binding.imgClear.apply {
                    visible()
                    click {
                        binding.autoSearch.setText("")
                    }
                }
            } else {
                binding.imgClear.gone()
            }
        }

        override fun afterTextChanged(p0: Editable?) {/**do nothing**/}

    }
    private fun navigateToChatWithUser(uid:String) {
        val bundle = Bundle()
        bundle.putString(Constants.KEY_USER_ID, uid)
        openFragment(ChatWithUserFragment::class.java, bundle, true)
    }

}