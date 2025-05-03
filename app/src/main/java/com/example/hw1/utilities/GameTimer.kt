package com.example.hw1.utilities

import android.os.Handler
import android.os.Looper

// Timer to activate ice falling in a random column
class GameTimer(
    private val dropInterval: Long = Constants.Game.CREATE_INTERVAL,
    private val onDrop: (column: Int) -> Unit,
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    // Runnable that repeatedly triggers a drop at random column every 'dropInterval' milliseconds
    private val gameRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return

            val col = (0 until Constants.Game.NUM_COLS).random() // Pick a random column
            onDrop(col) // Trigger the drop callback

            handler.postDelayed(this, dropInterval) // Schedule the next run
        }
    }

    // Starts the game timer if it is not already running
    fun start() {
        if (!isRunning) {
            isRunning = true
            handler.post(gameRunnable)
        }
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(gameRunnable)
    }
}