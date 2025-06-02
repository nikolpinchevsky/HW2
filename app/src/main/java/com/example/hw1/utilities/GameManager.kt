package com.example.hw1.utilities

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.example.hw1.R

class GameManager(
    private val gridViews: List<List<ImageView>>,
    private val onPlayerHit: () -> Unit,
    private val onScore: () -> Unit,
    private val getPlayerColumn: () -> Int,
    private var dropInterval: Long
) {
    private val numRows = gridViews.size
    private val numCols = gridViews[0].size
    private val columnInUse = BooleanArray(numCols) { false }

    var playerColumn = 1
        private set
    var lives = 3
        private set


    fun movePlayerLeft() {
        if (playerColumn > 0) {
            playerColumn--
            updatePlayerPosition()
        }
    }

    fun movePlayerRight() {
        if (playerColumn < numCols - 1) {
            playerColumn++
            updatePlayerPosition()
        }
    }

    fun updatePlayerPosition() {
        for (i in 0 until numCols) {
            val cell = gridViews[numRows - 1][i]
            if (i == playerColumn) {
                cell.visibility = View.VISIBLE
                cell.setImageResource(R.drawable.penguin)
            } else {
                cell.visibility = View.INVISIBLE
            }
        }
    }

    fun clearBoard() {
        for (row in gridViews) {
            for (cell in row) {
                cell.visibility = View.INVISIBLE
            }
        }
    }

    fun dropIce(column: Int) {
        if (columnInUse[column]) return
        columnInUse[column] = true

        var row = 0
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                if (row > 0 && !(row - 1 == numRows - 1 && column == playerColumn)) {
                    gridViews[row - 1][column].visibility = View.INVISIBLE
                }

                if (row == numRows - 1 && column == playerColumn) {
                    val cell = gridViews[row][column]
                    onPlayerHit()
                    cell.visibility = View.INVISIBLE
                    updatePlayerPosition()
                    columnInUse[column] = false
                    return
                }

                if (row < numRows) {
                    val cell = gridViews[row][column]
                    cell.visibility = View.VISIBLE
                    cell.setImageResource(R.drawable.ice)
                    row++
                    handler.postDelayed(this, dropInterval)
                } else {
                    columnInUse[column] = false
                }
            }
        }

        handler.post(runnable)
    }

    fun dropFish(column: Int) {
        if (columnInUse[column]) return
        columnInUse[column] = true

        var row = 0
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                if (row > 0 && !(row - 1 == numRows - 1 && column == playerColumn)) {
                    gridViews[row - 1][column].visibility = View.INVISIBLE
                }

                if (row == numRows - 1 && column == playerColumn) {
                    val cell = gridViews[row][column]
                    onScore()
                    cell.visibility = View.INVISIBLE
                    updatePlayerPosition()
                    columnInUse[column] = false
                    return
                }

                if (row < numRows) {
                    val cell = gridViews[row][column]
                    cell.visibility = View.VISIBLE
                    cell.setImageResource(R.drawable.fish)
                    row++
                    handler.postDelayed(this, dropInterval)
                } else {
                    columnInUse[column] = false
                }
            }
        }

        handler.post(runnable)
    }

    fun decreaseLife() {
        lives--
    }

    fun isColumnInUse(column: Int): Boolean = columnInUse[column]

    fun setDropInterval(newInterval: Long) {
        this.dropInterval = newInterval
    }
}