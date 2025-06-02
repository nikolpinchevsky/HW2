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
    private var speedFactor = 1.0f

    private val gameRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return

            val availableIceCols = (0 until Constants.Game.NUM_COLS).filter { !isColumnBlocked(it) }
            if (availableIceCols.isNotEmpty()) {
                val iceCol = availableIceCols.random()
                onDropIce(iceCol)

                if (Random.nextFloat() < 0.85f) {
                    val availableFishCols = availableIceCols.filter { it != iceCol }
                    if (availableFishCols.isNotEmpty()) {
                        val fishCol = availableFishCols.random()
                        onDropFish(fishCol)
                    }
                }
            }

            val adjustedInterval = (dropInterval / speedFactor).toLong()
            handler.postDelayed(this, adjustedInterval)
        }
    }

    // Starts the game timer if it is not already running
    fun start() {
        if (!isRunning) {
            isRunning = true
            val adjustedInterval = (dropInterval / speedFactor).toLong()
            handler.postDelayed(gameRunnable, adjustedInterval)
        }

    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(gameRunnable)
    }

    fun setSpeedFactor(factor: Float) {
        speedFactor = factor.coerceIn(0.3f, 4.0f)

        if (isRunning) {
            handler.removeCallbacks(gameRunnable)
            val adjustedInterval = (dropInterval / speedFactor).toLong()
            handler.postDelayed(gameRunnable, adjustedInterval)
        }
    }
}