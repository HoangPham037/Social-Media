package com.example.socialmedia.ui.chat.chatscreen.chatwithuser

import android.os.Handler
import android.os.Looper


class Timer(listener: OnTimeClickListener) {

    interface OnTimeClickListener {
        fun onTimeTick(duration: String)
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var duration = 0L
    private var delay = 100L

    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable, delay)
            listener.onTimeTick(format())
        }
    }

    fun start() {
        handler.postDelayed(runnable, delay)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        duration = 0L
    }

    private fun format(): String {
        val second = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        return "%02d:%02d".format(minutes, second)
    }
}