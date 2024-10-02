package com.example.socialmedia.model

import com.example.socialmedia.ui.notifation.testnofi.PushNotification
import kotlinx.serialization.Serializable

@Serializable
data class Turnofnotification(
    val pushNotification: PushNotification,
    val listTurnOffNotify: MutableList<MuteNotification>
)
