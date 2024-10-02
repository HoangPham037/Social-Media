package com.example.socialmedia.ui.chat.chatscreen.searchconversion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.hideKeyboard
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.FragmentSearchConversionBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserFragment
import com.example.socialmedia.ui.utils.Constants
import org.koin.android.ext.android.inject

class SearchConversionFragment : BaseFragmentWithBinding<FragmentSearchConversionBinding>() {

    private val searchViewModels: SearchConversionViewModel by inject()
    private val mainViewModel: MainViewModel by inject()
    private lateinit var searchUserRecyclerAdapter: SearchUserRecyclerAdapter
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    val sharedPreferences: Preferences by inject()

    override fun getViewBinding(inflater: LayoutInflater): FragmentSearchConversionBinding {
        return FragmentSearchConversionBinding.inflate(layoutInflater)
    }

    override fun init() {
    }

    override fun initData() {
        searchViewModels.fetchListSearchHistory(mainViewModel.getUid()!!)
        searchViewModels.listHistoryProfile.observe(viewLifecycleOwner) { listProfile ->
            if (listProfile.isEmpty()) {
                binding.layoutHistorySearch.rcHistorySearch.gone()
                binding.layoutHistorySearch.layoutNoDataHistory.visible()
                binding.layoutHistorySearch.tvDeleteHistorySearch.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey02
                    )
                )
            } else {
                binding.layoutHistorySearch.rcHistorySearch.visible()
                binding.layoutHistorySearch.layoutNoDataHistory.gone()
                searchHistoryAdapter = SearchHistoryAdapter(requireContext()) {
                    it.id?.let { it1 -> navigateToChatWithUser(it1) }
                }
                binding.layoutHistorySearch.rcHistorySearch.setHasFixedSize(true)
                binding.layoutHistorySearch.rcHistorySearch.adapter = searchHistoryAdapter
                searchHistoryAdapter.submitList(listProfile)
            }
        }
    }

    private fun searchUser() {
        binding.apply {
            autoSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val queryText = s.toString().trim()
                    searchUser(queryText)
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

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun searchUser(queryText: String) {
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
                            searchViewModels.addToSearchHistory(mainViewModel.getUid()!!,userInfo)
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
    }

    override fun initAction() {
        binding.tvCancel.click {
            onBackPressed()
        }
        searchUser()
        binding.layoutHistorySearch.tvDeleteHistorySearch.click {
            searchViewModels.deleteAllHistory()
        }
    }

    private fun navigateToChatWithUser(id: String) {
        view?.hideKeyboard(requireActivity())
        val bundle = Bundle()
        bundle.putString(Constants.KEY_USER_ID, id)
        openFragment(ChatWithUserFragment::class.java, bundle, true)
    }
}