package com.duallive.app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.view.Surface

object ScreenCastUtils {

    const val SCREEN_CAPTURE_REQUEST_CODE = 9001

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var projectionManager: MediaProjectionManager? = null

    /**
     * Ask user for screen capture permission
     */
    fun requestScreenCapture(activity: Activity) {
        projectionManager =
            activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
                    as MediaProjectionManager

        val intent = projectionManager!!.createScreenCaptureIntent()
        activity.startActivityForResult(
            intent,
            SCREEN_CAPTURE_REQUEST_CODE
        )
    }

    /**
     * Start capturing the screen after permission is granted
     */
    fun startScreenCapture(
        context: Context,
        resultCode: Int,
        data: Intent,
        surface: Surface
    ) {
        projectionManager =
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
                    as MediaProjectionManager

        mediaProjection =
            projectionManager?.getMediaProjection(resultCode, data)

        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "DualLiveScreenCast",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surface,
            null,
            null
        )
    }

    /**
     * Stop screen capture and clean resources
     */
    fun stopScreenCapture() {
        virtualDisplay?.release()
        virtualDisplay = null

        mediaProjection?.stop()
        mediaProjection = null
    }
}
