package com.example.hw1.utilities

import android.os.Handler
import android.os.Looper
import kotlin.random.Random


// Timer to activate ice falling in a random column
class GameTimer(
    private val dropInterval: Long = Constants.Game.CREATE_INTERVAL,
    private val onDropIce: (Int) -> Unit,
    private val onDropFish: (Int) -> Unit,
    private val isColumnBlocked: (Int) -> Boolean
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private val gameRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return

            val availableIceCols = (0 until Constants.Game.NUM_COLS).filter { !isColumnBlocked(it) }
            if (availableIceCols.isNotEmpty()) {
                val iceCol = availableIceCols.random()
                onDropIce(iceCol)

                if (Random.nextFloat() < 0.7f) {
                    val availableFishCols = availableIceCols.filter { it != iceCol }
                    if (availableFishCols.isNotEmpty()) {
                        val fishCol = availableFishCols.random()
                        onDropFish(fishCol)
                    }
                }
            }

            handler.postDelayed(this, dropInterval)
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