package com.example.socialmedia.ui.sreach

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.hideKeyboard
import com.example.socialmedia.databinding.FragmentSearchBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.model.ItemHomeSingleton
import com.example.socialmedia.ui.home.HomeAdapter
import com.example.socialmedia.ui.home.HomeViewModel
import com.example.socialmedia.ui.home.comment.CommentFragment
import com.example.socialmedia.model.Profile
import com.example.socialmedia.model.ProfileSigleton
import com.example.socialmedia.ui.home.TypeClick
import com.example.socialmedia.ui.profile.ProfileFragment
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.profile.ProfileViewModel
import com.example.socialmedia.ui.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class SearchFragment : BaseFragmentWithBinding<FragmentSearchBinding>() {

    private var listAccount: MutableList<String>? = mutableListOf()

    val viewModel: SearchViewModel by inject()
    val homeviewmodel: HomeViewModel by inject()
    val pro5viewmodel: ProfileViewModel by inject()
    lateinit var mAdapter: AdapterSearch
    lateinit var mAdapterTreding: AdapterTrendingSearch
    lateinit var mAdapterHistory: SearchHistoryAdapter
    lateinit var mAdapterHome: HomeAdapter
    val sharedPreferences: Preferences by inject()

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater)
    }

    override fun init() {
        SearchBar()
        setColorOfButton()
        binding.apply {
            mAdapterHome = HomeAdapter(homeviewmodel) { type, data ->
                when (type) {
                    TypeClick.TYPE_AVT -> {
                        showProfileFragment(data)
                    }

                    else -> {
                        showCommentFragment(data)
                    }
                }
            }
            rcvSearchHome.adapter = mAdapterHome
            rcvSearchHome.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            mAdapterHistory = SearchHistoryAdapter(requireContext())
            rcvHistory.adapter = mAdapterHistory
            rcvHistory.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            rcvHistory.setHasFixedSize(true)

            mAdapterHistory.setOnItemClickListener(object :
                SearchHistoryAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    view?.hideKeyboard(requireActivity())
                    val textseach = mAdapterHistory.mlist[position]
                    autocpl.setText(textseach)
                    searchUsers(textseach) { list ->
                        logicsearch(list)
                        if (list != null) {
                            for (i in list) {
                                i.name?.let {
                                    listAccount?.add(it)
                                }
                            }
                        }
                    }
                }
            })
            mAdapter = AdapterSearch(requireContext())
            rcvUsers.adapter = mAdapter
            rcvUsers.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            rcvUsers.setHasFixedSize(true)
            mAdapterTreding = AdapterTrendingSearch(requireContext())
            refreshLayout.setOnRefreshListener {
                refreshContent()
            }
        }
    }

    private fun refreshContent() {
        fetchData {
            mAdapter.notifyDataSetChanged()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun fetchData(onDataFetched: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            delay(2000) // Simulate a 2-second network call
            withContext(Dispatchers.Main) {
                onDataFetched()
            }
        }
    }

    private fun showProfileFragment(data: ItemHome) {
        val bundle = Bundle()
        data.userid?.let { id ->
            if (id == viewModel.uRepository.getUid()) {
                bundle.putString(Constants.KEY_TYPE, Constants.KEY_TYPE)
                openFragment(ProfileFragment::class.java, bundle, true)
            } else {
                bundle.putString(Constants.KEY_USERID, id)
                openFragment(ProfileFragmentUsers::class.java, bundle, true)
            }
        }
    }

    override fun initData() {
        addHistory()
    }

    override fun initAction() {
        gotouserprofile()
        mAdapter.setonitemclicklistener(object : AdapterSearch.OnItemClickListener {
            override fun onitemclick(position: Int) {
                val userId = mAdapter.listUsers[position].id
                val bundle = Bundle()
                bundle.putString(Constants.KEY_USERID, userId)
                bundle.putString(Constants.KEY_USERID, userId)
                openFragment(ProfileFragmentUsers::class.java, bundle, true)
            }
        })
        mAdapter.setOnButtonClickListener(object : AdapterSearch.OnButtonClickListener {
            override fun onButtonClick(position: Int) {
                val clickedItem = mAdapter.listUsers[position]
                pro5viewmodel.follow(clickedItem)
                clickedItem.isFollow = !clickedItem.isFollow
                mAdapter.notifyDataSetChanged()
            }
        })
        binding.apply {
            icBackSearch.click {
                llHistory.visibility = View.VISIBLE
                llSearch.visibility = View.GONE
                addHistory()
                mAdapterHistory.notifyDataSetChanged()
                autocpl.setText("")
            }
        }
        binding.refreshLayout.setOnRefreshListener {
            mAdapterHistory.notifyDataSetChanged()
        }
    }


    fun SearchBar() {
        val listIduser: MutableList<String> = mutableListOf()
        var listUsers: List<Profile>? = mutableListOf()
        binding.apply {
            autocpl.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val queryText = s.toString().trim()
                    if (queryText != "") {
                        llHistory.visibility = View.GONE
                        searchUsers(queryText) {
                            if (!it.isNullOrEmpty()) {
                                val uniqueUrls = it.distinctBy { "${it.id}" }
                                listIduser.clear()
                                for (i in uniqueUrls) {
                                    i.name?.let { userName ->
                                        if (!listIduser.contains(userName)) {
                                            listIduser.add(userName)
                                        }
                                    }
                                    recommendSearch(listIduser,1)
                                    showResultSearch(it, queryText)
                                    listUsers = it
                                }
                            } else {
                                showResultSearch(it, queryText)
                            }
                        }
                    } else {
                        llSearch.visibility = View.GONE
                        listIduser.clear()
                        showResultSearch(emptyList())
                        llHistory.visibility = View.VISIBLE
                        mAdapterHistory.notifyDataSetChanged()
                        addHistory()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    fun recommendSearch(list: List<String>,int: Int) {
        binding.apply {
            val adapterzz =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    list
                )
            autocpl.setAdapter(adapterzz)
            autocpl.threshold = int
        }
    }

    fun showResultSearch(list: List<Profile>?, query: String? = null) {
        binding.apply {
            autocpl.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == KeyEvent.KEYCODE_ENTER || actionId == KeyEvent.KEYCODE_SEARCH) {
                    true
                } else {
                    view?.hideKeyboard(requireActivity())
                    autocpl.setText(query)
                    recommendSearch(emptyList(),Int.MAX_VALUE)
                    if (query != null) {
                        addToSearchHistory(query)

                        mAdapterHome.listItem = emptyList()
                        mAdapterHome.listItem = searchPost(query)
                        Log.d("taisaolaisai", "${list}: ")
                    }
                    logicsearch(list)
                    if (list != null) {
                        mAdapter.listUsers = list
                        mAdapter.notifyDataSetChanged()
                    } else {
                        mAdapter.listUsers = emptyList()
                    }
                    false
                }
            }
        }
    }

    fun logicsearch(list: List<Profile>?) {
        binding.apply {
            btnAccount.background = resources.getDrawable(R.drawable.bg_round_boder_btn)
            btnHot.background = resources.getDrawable(R.drawable.bg_round_boder_btn2)
            llHistory.visibility = View.GONE
            llSearch.visibility = View.VISIBLE
            rcvSearchHome.visibility = View.GONE
            rcvUsers.visibility = View.VISIBLE
            if (list.isNullOrEmpty()) {
                llnodata.visibility = View.VISIBLE
                binding.rcvUsers.visibility = View.GONE
            } else {
                llnodata.visibility = View.GONE
                binding.rcvUsers.visibility = View.VISIBLE
                mAdapter.listUsers = list
            }
        }
    }

    fun setColorOfButton() {
        binding.apply {
            val listItemHot = arrayListOf<ItemHome>()
            val roundedButtonDrawable =
                resources.getDrawable(R.drawable.bg_round_boder_btn2)
            val roundedButtonDrawable1 =
                resources.getDrawable(R.drawable.bg_round_boder_btn)

            btnHot.setOnClickListener {
                if (mAdapterHome.listItem.isNullOrEmpty()) {
                    llnodata.visibility = View.VISIBLE
                } else {
                    llnodata.visibility = View.GONE
                }
                mAdapterHome.notifyDataSetChanged()
                rcvSearchHome.visibility = View.VISIBLE
                rcvUsers.visibility = View.GONE
                btnHot.background = roundedButtonDrawable1
                btnAccount.background = roundedButtonDrawable
            }

            btnAccount.setOnClickListener {
                if (mAdapter.listUsers.isNullOrEmpty()) {
                    llnodata.visibility = View.VISIBLE
                } else {
                    llnodata.visibility = View.GONE
                }
                rcvSearchHome.visibility = View.GONE
                rcvUsers.visibility = View.VISIBLE
                btnHot.background = roundedButtonDrawable
                btnAccount.background = roundedButtonDrawable1
            }
        }
    }

    fun gotouserprofile() {
        for (i in mAdapter.listUsers) {
        }
    }

    private fun addHistory() {
        val sharedPreferences =
            requireContext().getSharedPreferences(
                viewModel.uRepository.getUid(),
                Context.MODE_PRIVATE
            )
        val savedSearchHistory =
            sharedPreferences.getStringSet(viewModel.uRepository.getUid(), HashSet())
                ?.toList() ?: emptyList()
        val queryList = savedSearchHistory.toMutableList()
        if (queryList.isEmpty()) {
            binding.llnosearchhistory.visibility = View.VISIBLE
        } else {
            binding.llnosearchhistory.visibility = View.GONE
        }

        binding.deleteme.setOnClickListener {
            sharedPreferences.edit().remove(viewModel.uRepository.getUid()).apply()
            queryList.clear()
            binding.llnosearchhistory.visibility = View.VISIBLE
            mAdapterHistory.mlist = queryList as MutableList<String>
            mAdapterHistory.notifyDataSetChanged()
        }
        mAdapterHistory.mlist = queryList
        mAdapterHistory.notifyDataSetChanged()

    }

    private fun addToSearchHistory(query: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences(
                viewModel.uRepository.getUid(),
                Context.MODE_PRIVATE
            )
        val existingSearchHistory =
            sharedPreferences.getStringSet(viewModel.uRepository.getUid(), HashSet())
                ?.toMutableSet()
                ?: mutableSetOf()
        existingSearchHistory.add(query)
        sharedPreferences.edit()
            .putStringSet(viewModel.uRepository.getUid(), existingSearchHistory).apply()
        mAdapterHistory.notifyDataSetChanged()
    }

    fun searchPost(query: String): List<ItemHome> {
        val matchingItems = mutableListOf<ItemHome>()
        ItemHomeSingleton.getInstance {
            for (item in it) {
                item.content.let {
                    if (!it.isNullOrEmpty() && it.contains(query, ignoreCase = true)) {
                        matchingItems.add(item)
                    }
                }
            }
        }
        return matchingItems
    }

    fun searchUsers(query: String, callbacks: (List<Profile>?) -> Unit) {
        val matchItem = mutableListOf<Profile>()
        var listBlock = ArrayList<String>()
        var userPro5: Profile? = null
        matchItem.clear()
        ProfileSigleton.getInstance {
            for (item in it) {
                if (item.id == viewModel.uRepository.getUid()) {
                    userPro5 = item
                    if (item.id == viewModel.uRepository.getUid()) {
                        item.listBlock = listBlock
                        listBlock.add(item.id!!)
                    }

                }
                item.name.let {
                    if (!it.isNullOrEmpty() && it.contains(query, ignoreCase = true)) {
                        matchItem.add(item)
                    } else {
                        callbacks(emptyList())
                    }
                }
                val filterItem = matchItem.filter {
                    !listBlock.contains(it.id)
                }

                filterItem.forEach { i ->
                    i.isFollow = userPro5?.listFollowing?.contains(i.id) == true
                }

                callbacks(filterItem)
            }
        }
    }

    private fun showCommentFragment(itemHome: ItemHome) {
        if (view == null) return
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_ITEM_HOME, itemHome)
        openFragment(CommentFragment::class.java, bundle, true)
    }
}