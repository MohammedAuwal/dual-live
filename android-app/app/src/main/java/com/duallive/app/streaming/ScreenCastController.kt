package com.duallive.app.streaming

import android.content.Context
import android.content.Intent
import com.duallive.app.utils.ScreenCastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScreenCastController(
    private val context: Context
) {

    private var screenEncoder: ScreenEncoder? = null
    private var isRunning = false

    fun start(
        resultCode: Int,
        data: Intent
    ) {
        if (isRunning) return
        isRunning = true

        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        // 1️⃣ Prepare encoder
        screenEncoder = ScreenEncoder(
            width = width,
            height = height
        )

        val surface = screenEncoder!!.prepare()

        // 2️⃣ Start screen capture into encoder surface
        ScreenCastUtils.startScreenCapture(
            context = context,
            resultCode = resultCode,
            data = data,
            surface = surface
        )

        // 3️⃣ Drain encoded frames (for streaming later)
        CoroutineScope(Dispatchers.IO).launch {
            while (isRunning) {
                screenEncoder?.drainEncoder { buffer, info ->
                    // 🔥 Encoded H.264 data lives here
                    // RTMP / WebRTC will be plugged here later
                }
            }
        }
    }

    fun stop() {
        if (!isRunning) return
        isRunning = false

        ScreenCastUtils.stopScreenCapture()
        screenEncoder?.stop()
        screenEncoder = null
    }
}
