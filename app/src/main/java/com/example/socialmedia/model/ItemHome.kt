package com.example.socialmedia.model

import android.util.Log
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ItemHome(
    @SerializedName("key")
    val key: String? = null,
    val userid: String? = null,
    val content: String? = "",
    var urlImage: List<String> = arrayListOf(),
    var listUserLike: ArrayList<String> = arrayListOf(),
    var listShare: ArrayList<String> = arrayListOf(),
    var listCommnent: HashMap<String, Comment> = hashMapOf(),
    val datetime: Long? = null
) : Serializable

object ItemHomeSingleton {
    private var itemHome: ArrayList<ItemHome>? = null
    private var initializationCallback: ((ArrayList<ItemHome>) -> Unit)? = null

    fun initialize(data: ArrayList<ItemHome>, callback: ((ArrayList<ItemHome>) -> Unit)? = null) {
        itemHome = data
        initializationCallback?.invoke(itemHome ?: ArrayList())
        initializationCallback = null // Clear the callback after invoking
    }

    fun getInstance(callback: ((ArrayList<ItemHome>) -> Unit)) {
        if (itemHome != null) {
            callback.invoke(itemHome ?: ArrayList())
        } else {
            initializationCallback = callback
        }
    }
}
