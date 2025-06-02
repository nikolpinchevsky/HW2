package com.example.hw1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hw1.interfaces.TiltCallback
import com.example.hw1.models.GameScore
import com.example.hw1.models.ScoreManager
import com.example.hw1.utilities.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    private val bonusHandler = Handler(Looper.getMainLooper())
    private val bonusRunnable = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()
            if (now - lastHitTime >= 10_000) {
                updateScore(2)
                lastHitTime = now
            }
            bonusHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (!hasLocationPermission()) {
            requestLocationPermission()
        }
        DeviceLocation.getInstance().startLocationUpdates(this)

        findViews()

        val backgroundImageView = findViewById<ImageView>(R.id.main_background)
        ImageLoader.getInstance().loadImage(
            source = "https://i.pinimg.com/736x/b8/ea/8a/b8ea8a4ade69122089959929e3f80fd1.jpg",
            imageView = backgroundImageView
        )

        val isFast = intent.getBooleanExtra("IS_FAST", false)
        val withButtons = intent.getBooleanExtra("WITH_BUTTONS", true)
        useTiltControls = !withButtons

        val dropInterval = if (isFast) 200L else 800L
        val createInterval = if (isFast) 600L else 2000L

        if (!withButtons) {
            mainFabLeft.visibility = View.GONE
            mainFabRight.visibility = View.GONE
        } else {
            mainFabLeft.visibility = View.VISIBLE
            mainFabRight.visibility = View.VISIBLE
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

        val speedFactor = if (isFast) 2.0f else 1.0f
        gameTimer.setSpeedFactor(speedFactor)

        val newDropSpeed = if (isFast) 150L else 800L
        gameManager.setDropInterval(newDropSpeed)

        gameTimer.start()

        lastHitTime = System.currentTimeMillis()
        bonusHandler.post(bonusRunnable)
    }

    private fun findViews() {
        mainFabLeft = findViewById(R.id.mainFabLeft)
        mainFabRight = findViewById(R.id.mainFabRight)
        gridLayout = findViewById(R.id.game_board)

        mainImgHeart0 = findViewById(R.id.mainImgHeart0)
        mainImgHeart1 = findViewById(R.id.mainImgHeart1)
        mainImgHeart2 = findViewById(R.id.mainImgHeart2)

        scoreTextView = findViewById(R.id.mainScoreTextView)

        val views = List(Constants.Game.NUM_ROWS) { row ->
            List(Constants.Game.NUM_COLS) { col ->
                val index = row * Constants.Game.NUM_COLS + col
                gridLayout.getChildAt(index) as ImageView
            }
        }
        gridViews = views
    }

    private fun initViews() {
        mainFabLeft.setOnClickListener { gameManager.movePlayerLeft() }
        mainFabRight.setOnClickListener { gameManager.movePlayerRight() }
    }

    private fun loseLife() {
        lastHitTime = System.currentTimeMillis()
        SignalManager.toast(this, "Crash!")
        SignalManager.vibrate(this)

        val player = CrashSoundPlayer(this)
        player.playSound(R.raw.sound)

        gameManager.decreaseLife()

        when (gameManager.lives) {
            2 -> mainImgHeart0.visibility = View.INVISIBLE
            1 -> mainImgHeart1.visibility = View.INVISIBLE
            0 -> {
                mainImgHeart2.visibility = View.INVISIBLE
                gameTimer.stop()
                bonusHandler.removeCallbacks(bonusRunnable)

                val lat = DeviceLocation.getLat()
                val lon = DeviceLocation.getLng()

                val gameScore = GameScore(score, System.currentTimeMillis(), lat, lon)
                ScoreManager.saveScore(this, gameScore)

                val intent = Intent(this, GameOverActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
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
        bonusHandler.removeCallbacks(bonusRunnable)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}



