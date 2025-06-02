package com.example.hw1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hw1.R
import com.example.hw1.models.GameScore
import com.example.hw1.utilities.DateUtils

class HighScoreAdapter(
    private val scores: List<GameScore>,
    private val itemClickListener: (Double, Double) -> Unit
) : RecyclerView.Adapter<HighScoreAdapter.ScoreViewHolder>() {

    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeText: TextView = itemView.findViewById(R.id.score_item_place)
        val scoreText: TextView = itemView.findViewById(R.id.score_item_score)
        val dateText: TextView = itemView.findViewById(R.id.score_item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.score_item, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.placeText.text = (position + 1).toString()
        holder.scoreText.text = score.score.toString()
        holder.dateText.text = DateUtils.formatScoreDate(score)

        holder.itemView.setOnClickListener {
            itemClickListener(score.lat, score.lon)
        }
    }

    override fun getItemCount(): Int = scores.size
}