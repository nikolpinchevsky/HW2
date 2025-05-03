package com.example.hw1.utilities

import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hw1.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    private lateinit var mainFabLeft: FloatingActionButton
    private lateinit var mainFabRight: FloatingActionButton
    private lateinit var gridLayout: GridLayout

    private lateinit var mainImgHeart0: ImageView
    private lateinit var mainImgHeart1: ImageView
    private lateinit var mainImgHeart2: ImageView

    private lateinit var gameTimer: GameTimer
    private lateinit var gridViews: List<List<ImageView>>
    private var currentColumn = 1
    private var lives = 3
    private val columnInUse = BooleanArray(Constants.Game.NUM_COLS) { false }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViews()
        initViews()
        clearAllGrid()
        updatePlayerPosition()
        gameTimer = GameTimer(
            dropInterval = Constants.Game.CREATE_INTERVAL,
            onDrop = { column -> dropIce(column) },
        )
        gameTimer.start()
    }

    // Connect XML views to code variables
    private fun findViews() {
        mainFabLeft = findViewById(R.id.mainFabLeft)
        mainFabRight = findViewById(R.id.mainFabRight)
        gridLayout = findViewById(R.id.game_board)

        mainImgHeart0 = findViewById(R.id.mainImgHeart0)
        mainImgHeart1 = findViewById(R.id.mainImgHeart1)
        mainImgHeart2 = findViewById(R.id.mainImgHeart2)

        // Initializing the matrix of image views
        val views = List(6) { row ->
            List(3) { col ->
                val index = row * 3 + col
                gridLayout.getChildAt(index) as ImageView
            }
        }
        gridViews = views
    }

    // Setting left and right buttons
    private fun initViews() {
        mainFabLeft.setOnClickListener { moveLeft() }
        mainFabRight.setOnClickListener { moveRight() }
    }

    // Move player left if not already at the leftmost column
    private fun moveLeft() {
        if (currentColumn > 0) {
            currentColumn--
            updatePlayerPosition()
        }
    }

    // Move player right if not already at the rightmost column
    private fun moveRight() {
        if (currentColumn < 2) {
            currentColumn++
            updatePlayerPosition()
        }
    }

    // Making the relevant and irrelevant invisible
    private fun clearAllGrid() {
        for (row in gridViews) {
            for (cell in row) {
                cell.visibility = View.INVISIBLE
            }
        }
    }

    // Update the player's position in the bottom row
    private fun updatePlayerPosition() {
        for (i in 0..2) {
            val cell = gridViews[5][i]
            if (i == currentColumn) {
                cell.visibility = View.VISIBLE
                cell.setImageResource(R.drawable.penguin)
            } else {
                cell.visibility = View.INVISIBLE
            }
        }
    }

    // Ice falling on the screen
    private fun dropIce(column: Int) {
        columnInUse[column] = true
        var row = 0
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                if (row > 0) {
                    val prev = gridViews[row - 1][column]
                    // Hide previous ice block unless it hit the player
                    if (!(row - 1 == 5 && column == currentColumn)) {
                        prev.visibility = View.INVISIBLE
                    }
                }

                // Player hit by ice block
                if (row == 5 && column == currentColumn) {
                    columnInUse[column] = false
                    loseLife()
                    return
                }

                if (row <= 5) {
                    val cell = gridViews[row][column]
                    cell.visibility = View.VISIBLE
                    cell.setImageResource(R.drawable.ice)
                    updatePlayerPosition()
                    row++
                    handler.postDelayed(this, Constants.Game.DROP_INTERVAL)
                } else {
                    columnInUse[column] = false
                }
            }
        }

        handler.post(runnable)
    }

    // Restart the game by resetting lives and grid
    private fun restartGame() {
        lives = 3
        mainImgHeart0.visibility = View.VISIBLE
        mainImgHeart1.visibility = View.VISIBLE
        mainImgHeart2.visibility = View.VISIBLE

        clearAllGrid()
        currentColumn = 1
        updatePlayerPosition()
        gameTimer.start()
    }

    // Reduce life when hit by ice
    private fun loseLife() {
        SignalManager.toast(this, "Crash!")
        SignalManager.vibrate(this)

        lives--
        when (lives) {
            2 -> mainImgHeart0.visibility = View.INVISIBLE
            1 -> mainImgHeart1.visibility = View.INVISIBLE
            0 -> {
                mainImgHeart2.visibility = View.INVISIBLE
                gameTimer.stop()
                SignalManager.toast(this, "Game Over!")
                Handler(Looper.getMainLooper()).postDelayed({
                    restartGame()
                }, 500)
            }
        }
    }
}

