package com.informatoli.aupciel.game

import kotlin.math.min

sealed interface GameAction {
    data object Work : GameAction
    data object Eat : GameAction
    data object FreeTime : GameAction
    data object BuyBike : GameAction
    data object Restart : GameAction
}

object GameEngine {

    fun reduce(state: GameState, action: GameAction): GameState {
        if (action is GameAction.Restart) return GameState()
        if (state.result != GameResult.PLAYING) return state

        return when (action) {
            GameAction.Work -> work(state)
            GameAction.Eat -> eat(state)
            GameAction.FreeTime -> freeTime(state)
            GameAction.BuyBike -> buyBike(state)
            GameAction.Restart -> GameState()
        }
    }

    private fun work(state: GameState): GameState {
        if (state.energy < Balance.WORK_ENERGY_COST) {
            return state.copy(message = "No tienes energía suficiente para trabajar.")
        }
        if (state.blocks <= 0) return state

        return checkEndOfDay(
            state.copy(
                money = state.money + Balance.WORK_PAY,
                energy = state.energy - Balance.WORK_ENERGY_COST,
                blocks = state.blocks - 1,
                message = "+${Balance.WORK_PAY} € · -1 energía"
            )
        )
    }

    private fun eat(state: GameState): GameState {
        if (state.ateToday) return state.copy(message = "Ya has comido hoy.")
        if (state.money < Balance.FOOD_COST) {
            return state.copy(message = "No tienes dinero suficiente para comer.")
        }

        val blockCost = if (state.hasBike) 0 else 1
        if (state.blocks < blockCost) {
            return state.copy(message = "No tienes tiempo suficiente.")
        }

        return checkEndOfDay(
            state.copy(
                money = state.money - Balance.FOOD_COST,
                energy = min(Balance.MAX_ENERGY, state.energy + Balance.FOOD_ENERGY),
                blocks = state.blocks - blockCost,
                ateToday = true,
                message = if (state.hasBike)
                    "Has comido. La bici te ahorra tiempo."
                else
                    "Has comido: -4 € · +2 energía · -1 bloque"
            )
        )
    }

    private fun freeTime(state: GameState): GameState {
        if (state.blocks <= 0) return state

        return checkEndOfDay(
            state.copy(
                suns = state.suns + 1,
                blocks = state.blocks - 1,
                message = "Tiempo para ti: +1 ☀"
            )
        )
    }

    private fun buyBike(state: GameState): GameState {
        if (state.hasBike) return state.copy(message = "Ya tienes la bici.")
        if (state.money < Balance.BIKE_COST) {
            return state.copy(
                message = "Necesitas ${Balance.BIKE_COST} € para comprar la bici."
            )
        }

        return state.copy(
            money = state.money - Balance.BIKE_COST,
            hasBike = true,
            message = "🚲 Has comprado la bici. Comer ya no consume tiempo."
        )
    }

    private fun checkEndOfDay(state: GameState): GameState =
        if (state.blocks > 0) state else endDay(state)

    private fun endDay(state: GameState): GameState {
        if (state.money < Balance.RENT) {
            return state.copy(
                result = GameResult.DEFEAT,
                message = "No puedes pagar los ${Balance.RENT} € de alojamiento."
            )
        }

        val moneyAfterRent = state.money - Balance.RENT

        if (state.day == Balance.TOTAL_DAYS) {
            val victory =
                state.suns >= Balance.TARGET_SUNS &&
                moneyAfterRent >= Balance.FINAL_MIN_MONEY &&
                state.energy >= Balance.FINAL_MIN_ENERGY

            return state.copy(
                money = moneyAfterRent,
                result = if (victory) GameResult.VICTORY else GameResult.DEFEAT,
                message = if (victory)
                    "¡Victoria! Has conseguido tiempo para ti sin perder estabilidad."
                else
                    "La semana termina, pero no alcanzas todos los objetivos."
            )
        }

        return state.copy(
            day = state.day + 1,
            money = moneyAfterRent,
            blocks = Balance.DAILY_BLOCKS,
            ateToday = false,
            message = "Día ${state.day + 1}. Alojamiento: -${Balance.RENT} €."
        )
    }
}
