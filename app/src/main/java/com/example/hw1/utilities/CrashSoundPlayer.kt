package com.example.hw1.utilities

import android.content.Context
import android.media.MediaPlayer
import java.util.concurrent.Executors

class CrashSoundPlayer(context: Context) {

    private val context = context.applicationContext
    private val executor = Executors.newSingleThreadExecutor()

    fun playSound(resId: Int) {
        executor.execute {
            val mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer.isLooping = false
            mediaPlayer.setVolume(1.0f, 1.0f)
            mediaPlayer.setOnCompletionListener { mp ->
                mp.stop()
                mp.release()
            }
            mediaPlayer.start()
        }
    }
}