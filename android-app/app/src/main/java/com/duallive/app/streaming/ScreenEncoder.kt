package com.duallive.app.streaming

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.view.Surface
import java.nio.ByteBuffer

class ScreenEncoder(
    private val width: Int,
    private val height: Int,
    private val bitrate: Int = 4_000_000,
    private val frameRate: Int = 30
) {

    private var mediaCodec: MediaCodec? = null
    private var inputSurface: Surface? = null

    fun prepare(): Surface {
        val format = MediaFormat.createVideoFormat(
            MediaFormat.MIMETYPE_VIDEO_AVC,
            width,
            height
        )

        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        mediaCodec = MediaCodec.createEncoderByType(
            MediaFormat.MIMETYPE_VIDEO_AVC
        )

        mediaCodec?.configure(
            format,
            null,
            null,
            MediaCodec.CONFIGURE_FLAG_ENCODE
        )

        inputSurface = mediaCodec?.createInputSurface()
        mediaCodec?.start()

        return inputSurface!!
    }

    /**
     * Pull encoded video frames (H.264)
     */
    fun drainEncoder(onEncodedData: (ByteBuffer, MediaCodec.BufferInfo) -> Unit) {
        val bufferInfo = MediaCodec.BufferInfo()

        while (true) {
            val outputIndex = mediaCodec?.dequeueOutputBuffer(
                bufferInfo,
                0
            ) ?: break

            if (outputIndex >= 0) {
                val outputBuffer =
                    mediaCodec?.getOutputBuffer(outputIndex)

                if (outputBuffer != null && bufferInfo.size > 0) {
                    outputBuffer.position(bufferInfo.offset)
                    outputBuffer.limit(
                        bufferInfo.offset + bufferInfo.size
                    )

                    onEncodedData(outputBuffer, bufferInfo)
                }

                mediaCodec?.releaseOutputBuffer(outputIndex, false)
            } else {
                break
            }
        }
    }

    fun stop() {
        mediaCodec?.stop()
        mediaCodec?.release()
        mediaCodec = null

        inputSurface?.release()
        inputSurface = null
    }
}
