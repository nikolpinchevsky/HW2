package com.example.hw1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val replayButton: MaterialButton = findViewById(R.id.replay_button)
        val highScoresButton: MaterialButton = findViewById(R.id.high_scores_button)

        replayButton.setOnClickListener {
            val intent = Intent(this, FirstActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        highScoresButton.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }
}