package com.example.hw1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hw1.utilities.Constants
import com.example.hw1.utilities.GameManager
import com.example.hw1.utilities.GameTimer
import com.example.hw1.utilities.SignalManager
import com.example.hw1.scores.ScoreManager
import com.example.hw1.scores.GameScore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.hw1.utilities.ImageLoader
import com.example.hw1.utilities.TiltDetector
import com.example.hw1.interfaces.TiltCallback

class MainActivity : AppCompatActivity() {

    private lateinit var mainFabLeft: FloatingActionButton
    private lateinit var mainFabRight: FloatingActionButton
    private lateinit var gridLayout: GridLayout

    private lateinit var mainImgHeart0: ImageView
    private lateinit var mainImgHeart1: ImageView
    private lateinit var mainImgHeart2: ImageView

    private lateinit var gameManager: GameManager
    private lateinit var gameTimer: GameTimer

    private lateinit var gridViews: List<List<ImageView>>
    private lateinit var scoreTextView: TextView
    private var score = 0
    private var currentColumn = 1

    private var lastHitTime: Long = 0L

    private lateinit var tiltDetector: TiltDetector
    private var useTiltControls = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViews()

        ImageLoader.init(this)

       // val backgroundImageView = findViewById<ImageView>(R.id.main_background)

      //  ImageLoader.getInstance()
       //     .loadImage(
       //         source = "https://www.urbanbrush.net/web/wp-content/uploads/edd/2024/11/UB-202411150636.jpg",
       //         imageView = backgroundImageView
       //     )


        val isFast = intent.getBooleanExtra("IS_FAST", false)
        val withButtons = intent.getBooleanExtra("WITH_BUTTONS", true)

        val dropInterval = if (isFast) 400L else 800L
        val createInterval = if (isFast) 1000L else 2000L

        useTiltControls = intent.getBooleanExtra("WITHOUT_BUTTONS", false)

        if (!withButtons) {
            mainFabLeft.visibility = View.GONE
            mainFabRight.visibility = View.GONE
        }
        if (useTiltControls) {
            initTiltDetector()
        }


        gameManager = GameManager(
            gridViews = gridViews,
            dropInterval = dropInterval,
            getPlayerColumn = { currentColumn },
            onPlayerHit = { loseLife() },
            onScore = { updateScore() }
        )

        initViews()
        gameManager.clearBoard()
        gameManager.updatePlayerPosition()

        gameTimer = GameTimer(
            dropInterval = createInterval,
            onDropIce = { column -> gameManager.dropIce(column) },
            onDropFish = { column -> gameManager.dropFish(column) },
            isColumnBlocked = { column -> gameManager.isColumnInUse(column) }
        )

        gameTimer.start()

        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                val now = System.currentTimeMillis()
                if (now - lastHitTime >= 10_000) {
                    updateScore(2)
                    lastHitTime = now
                }
                Handler(Looper.getMainLooper()).postDelayed(this, 1000)
            }
        })
    }

    // Connect XML views to code variables
    private fun findViews() {
        mainFabLeft = findViewById(R.id.mainFabLeft)
        mainFabRight = findViewById(R.id.mainFabRight)
        gridLayout = findViewById(R.id.game_board)

        mainImgHeart0 = findViewById(R.id.mainImgHeart0)
        mainImgHeart1 = findViewById(R.id.mainImgHeart1)
        mainImgHeart2 = findViewById(R.id.mainImgHeart2)

        scoreTextView = findViewById(R.id.mainScoreTextView)

        // Initializing the matrix of image views
        val views = List(Constants.Game.NUM_ROWS) { row ->
            List(Constants.Game.NUM_COLS) { col ->
                val index = row * Constants.Game.NUM_COLS + col
                gridLayout.getChildAt(index) as ImageView
            }
        }
        gridViews = views
    }

    // Setting left and right buttons
    private fun initViews() {
        mainFabLeft.setOnClickListener { gameManager.movePlayerLeft() }
        mainFabRight.setOnClickListener { gameManager.movePlayerRight() }
    }



    private fun loseLife() {
        lastHitTime = System.currentTimeMillis()
        SignalManager.toast(this, "Crash!")
        SignalManager.vibrate(this)

        gameManager.decreaseLife()
        when (gameManager.lives) {
            2 -> mainImgHeart0.visibility = View.INVISIBLE
            1 -> mainImgHeart1.visibility = View.INVISIBLE
            0 -> {
                mainImgHeart2.visibility = View.INVISIBLE
                gameTimer.stop()
                ScoreManager.saveScore(this, GameScore(score = score, timestamp = System.currentTimeMillis()))
                val allScores = ScoreManager.getAllScores(this)
                SignalManager.toast(this, "Game Over!")
                Handler(Looper.getMainLooper()).postDelayed({
                    restartGame()
                }, 500)
            }
        }
    }

    private fun restartGame() {
        gameManager.resetLives()
        mainImgHeart0.visibility = View.VISIBLE
        mainImgHeart1.visibility = View.VISIBLE
        mainImgHeart2.visibility = View.VISIBLE

        gameManager.clearBoard()
        gameManager.resetPlayer()
        gameManager.updatePlayerPosition()

        score = 0
        scoreTextView.text = "000"

        gameTimer.start()
    }

    private fun updateScore(amount: Int = 5) {
        score += amount
        scoreTextView.text = String.format("%03d", score)
    }

    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback {
                override fun tiltLeft() {
                    gameManager.movePlayerLeft()
                }

                override fun tiltRight() {
                    gameManager.movePlayerRight()
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        if (useTiltControls) {
            tiltDetector.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (useTiltControls) {
            tiltDetector.stop()
        }
    }
}

