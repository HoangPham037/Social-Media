package com.example.socialmedia.ui.firstTimeSetUp

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.base.Adapter.RecyclerView.AdapterChoosePreferences
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.hideKeyboard
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.FragmentChoosePreferencesBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.home.HomeFragment
import com.example.socialmedia.ui.mainfragment.MainFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class ChoosePreferencesFragment : BaseFragmentWithBinding<FragmentChoosePreferencesBinding>() {

    private  val sharedPreferences: Preferences by inject()
    lateinit var mAdapter: AdapterChoosePreferences
    val viewmodel: MainViewModel by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentChoosePreferencesBinding {
        return FragmentChoosePreferencesBinding.inflate(layoutInflater)
    }

    override fun init() {
    }

    override fun initData() {
        setupAdapter()
        binding.apply {

        }
    }

    override fun initAction() {
        binding.apply {
            addPrefer.click {
                view?.hideKeyboard(requireActivity())
                sharedPreferences.setBoolean("checkPref",true)
                val uid = viewmodel.getUid()!!
                val update = mAdapter.getSelectedItems()
                viewmodel.addPref(update,"Users",uid) {
                    when(it) {
                        is State.Success -> openFragment(MainFragment::class.java,null,false)
                        else -> toast("Please choose at least one!")
                    }
                }


            }
            icBackChoosePref.click {}
        }
    }

    fun setupAdapter() {
        binding.apply {
            mAdapter = AdapterChoosePreferences()
            rcvPref.layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
            rcvPref.adapter = mAdapter
            setupClickAdapter()

            viewmodel.getDataFireStore("getPreference") {
                mAdapter.submitList(it)
            }
        }
    }

    /* fun customrcv() {
         val columnCount = 3
         val maxLengthItemCount = 2
         val layoutManager = GridLayoutManager(context, columnCount)
         layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
             override fun getSpanSize(position: Int): Int {

                 val item = mAdapter.list[position]
                 val itemLength = item.length // Replace with your logic to get the length of the item

                 if (itemLength > maxLengthItemCount && position % columnCount == 1) {
                     return columnCount - 1
                 }

                 return 1 // Default span size of 1 (occupy one column)
             }
         }
     }*/

    fun setupClickAdapter() {
        mAdapter.setOnItemClickListener(object : AdapterChoosePreferences.onClickListener {
            override fun onItemClickListener(position: Int) {
                val item = mAdapter.list[position]

                val countSelected = mAdapter.list.count { it.choose1 }
                if (item.choose1) {
                    item.choose1 = false
                    mAdapter.notifyItemChanged(position)
                } else if (countSelected < 3) {
                    item.choose1 = true
                    mAdapter.notifyItemChanged(position)
                }
            }
        })
    }
}