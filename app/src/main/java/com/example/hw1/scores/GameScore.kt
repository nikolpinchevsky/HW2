package com.example.hw1.scores

data class GameScore(
    val score: Int,
    val timestamp: Long
) {
    fun toStorageString(): String = "$score,$timestamp"

    companion object {
        fun fromStorageString(line: String): GameScore? {
            val parts = line.split(",")
            return if (parts.size == 2) {
                val score = parts[0].toIntOrNull()
                val time = parts[1].toLongOrNull()
                if (score != null && time != null) GameScore(score, time) else null
            } else null
        }
    }
}