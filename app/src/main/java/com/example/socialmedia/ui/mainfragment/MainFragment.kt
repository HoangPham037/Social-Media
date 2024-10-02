package com.example.socialmedia.ui.mainfragment


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.socialmedia.model.ActiveStatus
import com.example.socialmedia.databinding.FragmentMainBinding
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserViewModel
import com.example.socialmedia.ui.home.HomeFragment
import com.example.socialmedia.ui.home.post.PostFragment
import com.example.socialmedia.ui.minigame.MainActivityGame
import com.example.socialmedia.ui.notifation.NotificationFragment
import com.example.socialmedia.ui.profile.BottomAvatarFrg
import com.example.socialmedia.ui.profile.ProfileFragment
import com.example.socialmedia.ui.profile.block.BlockFragment
import com.example.socialmedia.ui.profile.follow.FollowFragment
import com.example.socialmedia.ui.setting.SettingFragment
import com.example.socialmedia.ui.sreach.SearchFragment
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWERS
import com.example.socialmedia.ui.utils.Constants.KEY_FOLLOWING
import com.example.socialmedia.ui.utils.Constants.KEY_PROFILE
import com.example.socialmedia.ui.utils.Constants.KEY_TYPE
import com.example.socialmedia.ui.utils.Constants.KEY_USERID
import org.koin.android.ext.android.inject
import com.example.socialmedia.R
import com.example.socialmedia.base.PermissionFragment
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.common.State
import com.example.socialmedia.model.ProfileSigleton
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.imagedetails.ImageDetailsFragment
import com.example.socialmedia.ui.home.HomeViewModel
import com.example.socialmedia.ui.profile.ChoosePictureFrg
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.setTextView

class MainFragment : PermissionFragment<FragmentMainBinding>(), View.OnClickListener {
    private val chatWithUserViewModel: ChatWithUserViewModel by inject()
    private val mainViewModel: MainViewModel by inject()
    private val viewModelHome: HomeViewModel by inject()
    private val listFragment = arrayListOf<Fragment>(
        HomeFragment(),
        SearchFragment.newInstance(),
        NotificationFragment.newInstance(),
        ProfileFragment.newInstance(),
    )

    companion object {
        var mainFragment: MainFragment? = null
        private var dialog: BottomAvatarFrg? = null
        fun newInstance() = MainFragment()
    }

    override fun onStart() {
        super.onStart()
        mainFragment = this
    }

    private var viewPagerAdapter: ViewPagerAdapter? = null

    override fun getViewBinding(inflater: LayoutInflater): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }

    private val viewModel: MainFragmentViewModel by inject()

    override fun init() {
        dialog = BottomAvatarFrg {
            onClick(it)
        }
        viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        chatWithUserViewModel.updateStatus(
            ActiveStatus.Online.name,
            mainViewModel.getUid()?: "",
            Constants.KEY_COLLECTION_USER
        )
        viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        binding.viewpager.adapter = viewPagerAdapter
        binding.viewpager.offscreenPageLimit = 4
        viewPagerAdapter?.setData(listFragment)
    }


    override fun initData() {
        viewModel.getProfileUser(viewModel.getUid())
        viewModel.livedataProfile.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.menu.tvName.text = it.name
                viewModel.setProfile(it)
                binding.menu.tvFollowers.text = setTextView(it.listFollowers?.size)
                binding.menu.tvFollowing.text = setTextView(it.listFollowing?.size)
                Glide.with(requireContext()).load(it.avtPath).fallback(R.drawable.avatar)
                    .into(binding.menu.ivProfile)

                viewModelHome.getUserData(viewModel.repository.getUid()!!)
                viewModelHome.userData.observe(this) {
                    binding.menu.txtDiamond.text = it.gold.toString()
                }
            }
        }
    }

    override fun initAction() {
        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.bottomNavigationView.changeSelectMenu(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        binding.bottomNavigationView.setOnClickItemClickListener {
            binding.viewpager.currentItem = it
        }
        binding.bottomNavigationView.setOnClickFloat {
            openFragment(PostFragment::class.java, null, true)
        }
        binding.apply {
            menu.ivProfile.click { openSheetDialog() }
        }
        binding.menu.tbHelp.setOnClickListener(this)
        binding.menu.tbBlock.setOnClickListener(this)
        binding.menu.tbSetting.setOnClickListener(this)
        binding.menu.ivCheckFb.setOnClickListener(this)
        binding.menu.tbProfile.setOnClickListener(this)
        binding.menu.ivCheckIns.setOnClickListener(this)
        binding.menu.ivCheckTwitter.setOnClickListener(this)
        binding.menu.ivCheckLinkedIn.setOnClickListener(this)
        binding.menu.tbFriendsNearby.setOnClickListener(this)
        binding.menu.tbMiniGame.setOnClickListener(this)
        binding.menu.menuLnFollowers.setOnClickListener(this)
        binding.menu.menuLnFollowing.setOnClickListener(this)
        binding.menu.tbBookmarks.setOnClickListener(this)
    }


    fun openDrawer() {
        binding.drawer.openDrawer(GravityCompat.START)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.menu_ln_following -> showFollowFragment(KEY_FOLLOWING)
            R.id.menu_ln_followers -> showFollowFragment(KEY_FOLLOWERS)
            R.id.tb_setting -> showSettingFragment()
            R.id.tb_view_avt -> showImageFragment()
            R.id.tb_mini_game -> startActivity(
                Intent(
                    requireActivity(),
                    MainActivityGame::class.java
                )
            )

            R.id.tb_change_avt -> openChoosePicFrg()
            R.id.tb_profile -> showProfileFragment()
            R.id.tb_block -> showBlockFragment()
            R.id.iv_check_fb -> toast("Tính năng đang được phát triển")
            R.id.iv_check_ins -> toast("Tính năng đang được phát triển")
            R.id.iv_check_linked_in -> toast("Tính năng đang được phát triển")
            R.id.iv_check_twitter -> toast("Tính năng đang được phát triển")
            R.id.tb_bookmarks -> toast("Tính năng đang được phát triển")
            R.id.tb_friends_nearby -> toast("Tính năng đang được phát triển")
            R.id.tb_help -> toast("Tính năng đang được phát triển")

        }
    }

    private fun showBlockFragment() {
        closeDrawers()
        if (viewModel.livedataProfile.value == null) return
        val bundle = Bundle()
        bundle.putSerializable(KEY_PROFILE, viewModel.livedataProfile.value)
        openFragment(BlockFragment::class.java, bundle, true)
    }

    private fun closeDrawers() {
        binding.drawer.closeDrawers()
        binding.drawer.setScrimColor(Color.TRANSPARENT)
    }

    private fun showProfileFragment() {
        closeDrawers()
        val bundle = Bundle()
        bundle.putString(KEY_TYPE, KEY_TYPE)
        openFragment(ProfileFragment::class.java, bundle, true)
    }

    private fun openChoosePicFrg() {
        closeDrawers()
        openFragment(ChoosePictureFrg::class.java, null, true)
    }


    private fun showImageFragment() {
        closeDrawers()
        if (viewModel.livedataProfile.value != null) {
            val bundle = Bundle()
            bundle.putString("imgUrl", viewModel.livedataProfile.value?.avtPath)
            openFragment(ImageDetailsFragment::class.java, bundle, true)
        }
    }

    private fun openSheetDialog() {
        dialog?.show(childFragmentManager, ProfileFragment::class.java.name)
    }

    private fun showSettingFragment() {
        closeDrawers()
        openFragment(SettingFragment::class.java, null, true)
    }

    private fun showFollowFragment(type: String) {
        closeDrawers()
        val bundle = Bundle()
        bundle.putString(KEY_TYPE, type)
        bundle.putString(KEY_USERID, viewModel.getUid())
        openFragment(FollowFragment::class.java, bundle, true)
    }
}
