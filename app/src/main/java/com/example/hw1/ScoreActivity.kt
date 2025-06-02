package com.example.hw1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hw1.ui.HighScoreFragment
import com.example.hw1.ui.MapFragment
import com.example.hw1.interfaces.Callback_HighScoreItemClicked
import com.google.android.material.button.MaterialButton

class ScoreActivity : AppCompatActivity(), Callback_HighScoreItemClicked {

    private lateinit var mapFragment: MapFragment
    private lateinit var highScoreFragment: HighScoreFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)

        mapFragment = MapFragment()
        highScoreFragment = HighScoreFragment()
        highScoreFragment.highScoreItemClicked = this

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_list, highScoreFragment)
            .replace(R.id.main_FRAME_map, mapFragment)
            .commit()

        val backToHomeButton: MaterialButton = findViewById(R.id.back_to_home_button)
        backToHomeButton.setOnClickListener {
            val intent = Intent(this, FirstActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun highScoreItemClicked(lat: Double, lon: Double) {
        mapFragment.zoomToLocation(lat, lon)
    }
}
