package com.example.hw1.utilities

import com.example.hw1.models.GameScore
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatScoreDate(score: GameScore): String {
        val date = Date(score.timestamp)
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
}
