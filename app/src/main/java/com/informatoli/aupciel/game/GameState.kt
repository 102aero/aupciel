package com.informatoli.aupciel.game

enum class GameResult {
    PLAYING,
    VICTORY,
    DEFEAT
}

data class GameState(
    val day: Int = 1,
    val money: Int = Balance.INITIAL_MONEY,
    val energy: Int = Balance.INITIAL_ENERGY,
    val blocks: Int = Balance.DAILY_BLOCKS,
    val suns: Int = 0,
    val hasBike: Boolean = false,
    val ateToday: Boolean = false,
    val result: GameResult = GameResult.PLAYING,
    val message: String =
        "Consigue 7 ☀ y termina el día 4 con al menos 10 € y energía 2."
)
