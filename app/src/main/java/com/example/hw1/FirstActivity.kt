package com.example.hw1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton

class FirstActivity : AppCompatActivity(){

    private lateinit var speedSwitch: SwitchCompat
    private lateinit var moveButtonSwitch: SwitchCompat
    private lateinit var startButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        findViews()
        initListeners()
    }

    private fun findViews() {
        speedSwitch = findViewById(R.id.speed_button)
        moveButtonSwitch = findViewById(R.id.move_button)
        startButton = findViewById(R.id.button)
    }

    private fun initListeners() {
        startButton.setOnClickListener {
            val isFast = speedSwitch.isChecked
            val withButtons = moveButtonSwitch.isChecked

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("IS_FAST", isFast)
                putExtra("WITH_BUTTONS", withButtons)
            }

            startActivity(intent)
            finish()
        }
    }
}