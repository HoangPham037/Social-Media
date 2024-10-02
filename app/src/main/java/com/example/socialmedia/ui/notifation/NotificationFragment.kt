package com.example.socialmedia.ui.notifation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentNotificationBinding
import com.example.socialmedia.model.Notification
import com.example.socialmedia.model.Profile
import com.example.socialmedia.model.ProfileSigleton
import com.example.socialmedia.ui.home.comment.CommentFragment
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.profile.ProfileViewModel
import com.example.socialmedia.ui.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class NotificationFragment : BaseFragmentWithBinding<FragmentNotificationBinding>(),
    AdapterNotifications.OnclickListener {

    lateinit var mAdapter: AdapterNotifications

    companion object {
        fun newInstance() = NotificationFragment()
    }

    private val viewModel: NotificationViewModel by inject()
    val pro5viewmodel: ProfileViewModel by inject()
    override fun getViewBinding(inflater: LayoutInflater): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(inflater)
    }

    override fun init() {
        viewModel.gettoken()
    }

    override fun initData() {
        binding.apply {
            mAdapter = AdapterNotifications(requireContext(), this@NotificationFragment)
            rcvNotification.adapter = mAdapter
            rcvNotification.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            rcvNotification.setHasFixedSize(true)

        }
        viewModel.checkpostdata()
        viewModel.newnotifi.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.clearNewNotifications()
            }
        }
        viewModel.checknewNofi()
        viewModel.livedatatest.observe(viewLifecycleOwner) {
            val uniqueUrls = it.distinctBy { "${it.type}${it.senderUid}" }

            mAdapter.submitlist(uniqueUrls)
            if (mAdapter.list.isEmpty()) {
                binding.llnonotifications.visibility = View.VISIBLE
            } else {
                binding.llnonotifications.visibility = View.GONE
            }
        }
        ProfileSigleton.getInstance { profile ->
            var userpro5 : Profile? = null
            for (item in profile) {
                if(item.id == viewModel.uRepository.getUid()){
                    userpro5 = item
                }
            }
            mAdapter.list.forEach {
                it.ispostnotification = userpro5?.listFollowing?.contains(it.senderUid) == true
            }
        }
        mAdapter.setOnButtonClickListener(object : AdapterNotifications.OnButtonClickListener {
            override fun onButtonClick(position: Int) {
                val clickedItem = mAdapter.list[position]
                var pro5 : Profile? =null
                clickedItem.ispostnotification = !clickedItem.ispostnotification
                ProfileSigleton.getInstance {
                    for (i in it){
                        if(mAdapter.list[position].senderUid == i.id){
                            pro5 = i
                            pro5!!.isFollow = !pro5!!.isFollow
                        }
                    }
                }
                pro5viewmodel.follow(pro5!!)

                mAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun initAction() {
        binding.apply {

        }
    }

    override fun onCommentNotificationClick(notification: Notification) {
        setReadNotificationState(notification)
        val bundle = Bundle()
        viewModel.getpostwithkey(notification.idPost!!) {
            bundle.putSerializable(Constants.KEY_ITEM_HOME, it)
            openFragment(CommentFragment::class.java, bundle, true)
        }
    }

    override fun onFollowNotificationClick(notification: Notification) {
        val bundle = Bundle()
        setReadNotificationState(notification)
        bundle.putString(Constants.KEY_USERID, notification.senderUid)
        openFragment(ProfileFragmentUsers::class.java, bundle, true)
    }

    override fun onLikeNotificationClick(notification: Notification) {
        setReadNotificationState(notification)
        val bundle = Bundle()
        viewModel.getpostwithkey(notification.idPost!!) {
            bundle.putSerializable(Constants.KEY_ITEM_HOME, it)
            openFragment(CommentFragment::class.java, bundle, true)
        }
    }

    fun setReadNotificationState(notification: Notification) {
        val key = notification.key
        val updatate: HashMap<String, Any?> = hashMapOf("isread" to true)
        viewModel.updatedata(updatate, key, "Notifications")
    }

}