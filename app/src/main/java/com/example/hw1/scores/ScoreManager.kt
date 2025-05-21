package com.example.hw1.scores

import android.content.Context

object ScoreManager {
    private const val SCORES_KEY = "SCORES_KEY"

    fun saveScore(context: Context, newScore: GameScore) {
        val sharedPrefs = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val existingData = sharedPrefs.getString(SCORES_KEY, "") ?: ""

        val updatedData = existingData + "\n" + newScore.toStorageString()

        sharedPrefs.edit().putString(SCORES_KEY, updatedData.trim()).apply()
    }

    fun getAllScores(context: Context): List<GameScore> {
        val sharedPrefs = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val rawData = sharedPrefs.getString(SCORES_KEY, "") ?: ""

        return rawData
            .lines()
            .mapNotNull { GameScore.fromStorageString(it.trim()) }
            .sortedByDescending { it.score }
    }
}