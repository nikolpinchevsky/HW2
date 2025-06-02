package com.example.hw1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hw1.R
import com.example.hw1.adapters.HighScoreAdapter
import com.example.hw1.interfaces.Callback_HighScoreItemClicked
import com.example.hw1.models.ScoreManager

class HighScoreFragment : Fragment() {

    var highScoreItemClicked: Callback_HighScoreItemClicked? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_score, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.highscore_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val scores = ScoreManager.getTop5Scores(requireContext()).take(10) // או getTop10Scores()
        val adapter = HighScoreAdapter(scores) { lat, lon ->
            highScoreItemClicked?.highScoreItemClicked(lat, lon)
        }

        recyclerView.adapter = adapter

        return view
    }
}


