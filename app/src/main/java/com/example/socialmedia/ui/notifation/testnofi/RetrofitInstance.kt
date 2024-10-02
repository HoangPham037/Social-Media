package com.example.socialmedia.ui.notifation.testnofi

import com.example.socialmedia.model.Notification
import com.example.socialmedia.ui.minigame.di.Constant.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}

@kotlinx.serialization.Serializable
data class PushNotification(
    val data: Notification,
    val to: String
)


