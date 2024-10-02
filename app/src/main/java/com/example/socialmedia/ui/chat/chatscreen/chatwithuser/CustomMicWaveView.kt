package com.example.socialmedia.ui.chat.chatscreen.chatwithuser

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CustomMicWaveView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()
    private var radius = 6f
    private var w = 9f
    private var d = 6f
    private var maxSpike = 0

    init {
        paint.color = Color.BLUE
        maxSpike = (resources.displayMetrics.widthPixels.toFloat() / (w + d + 8)).toInt()
    }

    fun addAmplitudes(amp: Float) {
        val norm = Math.min(amp.toInt() / 20, 30).toFloat()

        amplitudes.add(norm)

        spikes.clear()
        val amps = amplitudes.takeLast(maxSpike)
        for (i in amps.indices) {
            val l = 0 + i * (w + d)
            val t = 90/2 - amps[i]/2
            val r = l + w
            val b = t + amps[i]
            spikes.add(RectF(l, t, r, b))
        }

        invalidate()

    }

    fun clear() : ArrayList<Float> {
        val amps = amplitudes.clone() as ArrayList<Float>
        amplitudes.clear()
        spikes.clear()
        invalidate()
        return amps
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        spikes.forEach {
            canvas.drawRoundRect(it, radius, radius, paint)
        }
    }
}