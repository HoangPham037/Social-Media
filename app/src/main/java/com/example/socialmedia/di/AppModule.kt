package com.example.socialmedia.di

import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.ui.chat.chatscreen.ChatViewModel
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.ChatWithUserViewModel
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.viewprofile.ViewProfileViewModel
import com.example.socialmedia.ui.chat.chatscreen.searchconversion.SearchConversionViewModel
import com.example.socialmedia.ui.home.comment.CommentViewmodel
import com.example.socialmedia.ui.home.HomeViewModel
import com.example.socialmedia.ui.home.post.PostViewModel
import com.example.socialmedia.ui.home.post.gallery.GalleryImageViewModel
import com.example.socialmedia.ui.loginstuff.LoginStuffViewModel
import com.example.socialmedia.ui.mainfragment.MainFragmentViewModel
import com.example.socialmedia.ui.minigame.detail_question.DetailQuestionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.example.socialmedia.ui.notifation.NotificationViewModel
import com.example.socialmedia.ui.profile.ChoosePictureModel
import com.example.socialmedia.ui.profile.ProfileViewModel
import com.example.socialmedia.ui.profile.account.AccountViewModel
import com.example.socialmedia.ui.profile.follow.user.FollowUserViewModel
import com.example.socialmedia.ui.profile.follow.FollowViewModel
import com.example.socialmedia.ui.sreach.SearchViewModel
import com.facebook.CallbackManager

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object appModul {

    val applicationModule: Module = module {

        single<Repository> {
            Repository(get(), get(), get())
        }

        single { FirebaseFirestore.getInstance() }

        single { Firebase.auth }

        single { FirebaseAuth.getInstance() }

        single<CallbackManager> { CallbackManager.Factory.create() }

        single<Preferences> {
            Preferences.getInstance(get())
        }
        viewModel {
            PostViewModel(get())
        }
        viewModel {
            MainViewModel(get(), get())
        }
        viewModel {
            NotificationViewModel(get())
        }
        viewModel {
            SearchViewModel(get())
        }
        viewModel {
            HomeViewModel(get())
        }
        viewModel {
            GalleryImageViewModel(get(), get())
        }
        viewModel {
            FollowViewModel(get())
        }
        viewModel {
            AccountViewModel(get())
        }
        viewModel { LoginStuffViewModel(get()) }
        viewModel { MainFragmentViewModel(get()) }

        viewModel {
            ChatViewModel(get() ,get())
        }
        viewModel {
            ChatWithUserViewModel(get(),get())
        }
        viewModel {
            SearchConversionViewModel(get(),get ())
        }

        viewModel { CommentViewmodel(get()) }
        viewModel {
            ViewProfileViewModel(get())
        }
        viewModel {
            DetailQuestionViewModel(get())
        }
        viewModel {
            FollowUserViewModel(get())
        }
        viewModel {
            ProfileViewModel(get())
        }
        viewModel {
            ChoosePictureModel(get() )
        }
    }
}