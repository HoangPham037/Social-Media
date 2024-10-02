package com.example.socialmedia.ui.notifation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R

lateinit var notificationChannel: NotificationChannel
lateinit var notificationManager: NotificationManager

fun Context.showNotification(channelId: String, title: String, body: String) {
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val intent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationChannel =
            NotificationChannel(channelId, body, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.BLUE
                enableVibration(true)
            }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    val largeIconBitmap = BitmapFactory.decodeResource(resources, R.drawable.avatar)
    val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId).apply {
        setSmallIcon(R.drawable.ic_avt1)
        setContentTitle(title)
        setLargeIcon(largeIconBitmap)
        setContentText(body)
        setAutoCancel(true)
        setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        setContentIntent(pendingIntent)
    }

    notificationManager.notify(channelId.toInt(), builder.build())
}