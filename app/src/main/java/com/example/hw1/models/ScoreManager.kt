package com.example.hw1.models

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Context

object ScoreManager {
    private const val SCORES_KEY = "TOP_10_SCORES"

    fun saveScore(context: Context, newScore: GameScore) {
        val sharedPrefs = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val gson = Gson()

        val existingData = sharedPrefs.getString(SCORES_KEY, null)
        val type = object : TypeToken<MutableList<GameScore>>() {}.type
        val scores: MutableList<GameScore> = gson.fromJson(existingData, type) ?: mutableListOf()

        scores.add(newScore)
        scores.sortByDescending { it.score }
        val top10 = scores.take(10)

        val updatedJson = gson.toJson(top10)
        sharedPrefs.edit().putString(SCORES_KEY, updatedJson).apply()
    }

    fun getTop5Scores(context: Context): List<GameScore> {
        val sharedPrefs = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val json = sharedPrefs.getString(SCORES_KEY, null) ?: return emptyList()

        val type = object : TypeToken<List<GameScore>>() {}.type
        return Gson().fromJson(json, type)
    }
}