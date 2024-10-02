package com.example.socialmedia.di

import android.app.Application
import com.example.socialmedia.di.appModul.applicationModule
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication: Application() {

    companion object{
        private lateinit var instance : BaseApplication

        fun getInstance(): BaseApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this // Initialize instance
        startKoin{
            androidLogger()
            androidContext(this@BaseApplication)
            modules(applicationModule)
        }
        EmojiManager.install(GoogleEmojiProvider())
    }
}

