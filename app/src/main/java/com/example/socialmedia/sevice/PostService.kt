package com.nds.connectinglonelydays.service


import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.example.socialmedia.R
import com.example.socialmedia.common.State
import com.example.socialmedia.model.Comment
import com.example.socialmedia.model.ImageModel
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.ui.utils.getTimeDate
import com.google.firebase.appcheck.internal.util.Logger.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.File
import java.util.UUID

class PostService : Service() {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val CHANNEL_ID = "POST_NOTIFICATION"
    private var content: String? = null
    private val repository: Repository by inject()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bitmap = intent?.extras?.get("image") as? ArrayList<ImageModel>
        content = intent?.extras?.getString("content")
        build(bitmap)
        return START_STICKY
    }


    @SuppressLint("ForegroundServiceType")
    private fun build(data: ArrayList<ImageModel>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.post_notifi)
            val descriptionText = getString(R.string.channel_post_notifi)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Đang tạo bài viết")
            .setSmallIcon(R.drawable.ic_bell)
        if (data != null) {
            postImage(applicationContext,data, notification){
                newPost(applicationContext, it , notification)
            }
        }else{
            newPost(applicationContext , notification =  notification)
        }
        try{
            startForeground(1, notification.build())
        }catch(ex: Throwable){

        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun postImage(context: Context, data: MutableList<ImageModel>, notification: NotificationCompat.Builder , callback : (listUrl : MutableList<String>)-> Unit) {
        scope.launch (Dispatchers.IO){
            val listData : MutableList<String> = mutableListOf()
            withContext(Dispatchers.Main){
                notification.setContentTitle("Đang tạo bài viết vui lòng chờ")
                startForeground(1, notification.build())
            }

            data.forEach {
                repository.upLoadImageHome(Uri.fromFile(File(it.uri))){
                    when (it) {
                        is State.Success -> {
                            listData.add(it.data)
                            if (listData.size == data.size){
                                callback.invoke(listData)
                            }
                        }

                        is State.Error-> {
                            listData.add("")
                            if (listData.size  == data.size){
                                callback.invoke(listData)
                            }
                        }


                        else -> {

                        }
                    }

                }

            }
            }

    }

    @SuppressLint("ForegroundServiceType")
    private fun newPost(context: Context, listUrl: MutableList<String> ? = null, notification: Builder){
        val listUserLike : ArrayList<String> = arrayListOf()
        val listShare : ArrayList<String> = arrayListOf()
        val listCommnent: HashMap<String, Comment> = hashMapOf()
        val itemHome = ItemHome(UUID.randomUUID().toString(), repository.getUid(), content, listUrl as? ArrayList<String> ?: arrayListOf(), listUserLike, listShare, listCommnent, System.currentTimeMillis())
        scope.launch (Dispatchers.IO){
            repository.addItemHome(itemHome){
                when(it){
                    is State.Success -> {
                         postNotif(context)
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    }
                    is State.Error ->{
                        notification.setContentTitle("Nhấp để tải lên")
                        startForeground(1, notification.build())
                    }
                    else -> {}
                }
            }
        }
    }

    fun postNotif(context: Context) {
        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Đã tạo xong bài viết")
            .setContentText("Chạm để mở bài viết")
            .setSmallIcon(R.drawable.ic_bell)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.post_notifi)
            val descriptionText = getString(R.string.channel_post_notifi)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
        NotificationManagerCompat.from(this).apply {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            this.notify(getTimeDate().toInt(), builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}

