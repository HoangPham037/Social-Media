package com.example.socialmedia.ui.notifation.testnofi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.ConversionSingleTon
import com.example.socialmedia.model.PostNotification
import com.example.socialmedia.model.Turnofnotification
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.chat.chatscreen.ChatFragment
import com.example.socialmedia.ui.notifation.NotificationViewModel
import com.example.socialmedia.ui.profile.ProfileFragmentUsers
import com.example.socialmedia.ui.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"
class FirebaseService : FirebaseMessagingService() {

    private val viewModel: NotificationViewModel by inject()
    val sharedPreferences : Preferences by inject()
    companion object {
        private const val REPLY_ACTION_ID = "REPLY_ACTION_ID"
        private const val KEY_REPLY_TEXT = "KEY_REPLY_TEXT"


        var sharedPreferences: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPreferences?.getString("token", "")
            }
            set(value) {
                sharedPreferences?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("minhhin", "i run here2: ")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val resultIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("NavigateFrag", message.data["type"])
            putExtra(Constants.KEY_ITEM_HOME, message.data["idPost"])
            putExtra(Constants.KEY_USERID, message.data["senderUid"])
        }
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val sharedCustomPref = Preferences()
        sharedCustomPref.setIntValue("values", notificationID)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(Html.fromHtml("<b>${message.data["name"]}</b>: ${message.data["content"]}"))
            .setSmallIcon(R.drawable.ic_logo1)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .build()

        if(message.data["type"] != "message") {
            notificationManager.notify(notificationID, notification)
        } else{
            ConversionSingleTon.getInstance {
                for (i in it) {
                    val senderUid = message.data["senderUid"]
                    if ((i.listUid!![0] == senderUid && !i.isMuteReversion!!) || (i.listUid!![1] == senderUid && !i.isMuteSend!!)) {
                        notificationManager.notify(notificationID, notification)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        Log.d("minhhin", "onMessageReceived: ear run high")
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}