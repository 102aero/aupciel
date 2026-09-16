package com.informatoli.aupciel.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.informatoli.aupciel.game.Balance
import com.informatoli.aupciel.game.GameAction
import com.informatoli.aupciel.game.GameEngine
import com.informatoli.aupciel.game.GameResult
import com.informatoli.aupciel.game.GameState

private val Cream = Color(0xFFF6F0E6)
private val Ivory = Color(0xFFFFF9F1)
private val Terracotta = Color(0xFFD97855)
private val Sage = Color(0xFF7FA58A)
private val Gold = Color(0xFFE7B85C)
private val SoftBlue = Color(0xFF8DAFC0)
private val Charcoal = Color(0xFF26333A)
private val WarmGray = Color(0xFF69736F)
private val Danger = Color(0xFFC95D5D)

private enum class Place {
    HOME,
    WORK,
    MARKET
}

@Composable
fun UrbanCozyGameScreen() {
    var state by remember { mutableStateOf(GameState()) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Cream
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                val wide = maxWidth >= 700.dp

                if (wide) {
                    WideLayout(
                        state = state,
                        selectedPlace = selectedPlace,
                        onPlaceSelected = { selectedPlace = it },
                        onAction = {
                            state = GameEngine.reduce(state, it)
                        },
                        onRestart = {
                            state = GameEngine.reduce(state, GameAction.Restart)
                            selectedPlace = null
                        }
                    )
                } else {
                    CompactLayout(
                        state = state,
                        selectedPlace = selectedPlace,
                        onPlaceSelected = { selectedPlace = it },
                        onAction = {
                            state = GameEngine.reduce(state, it)
                        },
                        onRestart = {
                            state = GameEngine.reduce(state, GameAction.Restart)
                            selectedPlace = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WideLayout(
    state: GameState,
    selectedPlace: Place?,
    onPlaceSelected: (Place) -> Unit,
    onAction: (GameAction) -> Unit,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Hud(state)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Board(
                state = state,
                selectedPlace = selectedPlace,
                onPlaceSelected = onPlaceSelected,
                modifier = Modifier
                    .weight(1.55f)
                    .fillMaxHeight()
            )

            ActionPanel(
                state = state,
                selectedPlace = selectedPlace,
                onAction = onAction,
                onRestart = onRestart,
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun CompactLayout(
    state: GameState,
    selectedPlace: Place?,
    onPlaceSelected: (Place) -> Unit,
    onAction: (GameAction) -> Unit,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Hud(state)

        Board(
            state = state,
            selectedPlace = selectedPlace,
            onPlaceSelected = onPlaceSelected,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        ActionPanel(
            state = state,
            selectedPlace = selectedPlace,
            onAction = onAction,
            onRestart = onRestart,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Hud(state: GameState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Ivory),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Día ${state.day}/${Balance.TOTAL_DAYS}",
                        color = Charcoal,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 23.sp
                    )
                    Text(
                        text = "A un sueldo del cielo",
                        color = WarmGray,
                        fontSize = 14.sp
                    )
                }

                ObjectiveChip(state)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HudChip(
                    label = "Dinero",
                    value = "${state.money} €",
                    modifier = Modifier.weight(1f)
                )

                HudChip(
                    label = "Energía",
                    value = "${state.energy}/${Balance.MAX_ENERGY}",
                    modifier = Modifier.weight(1f)
                )

                HudChip(
                    label = "Tiempo",
                    value = blocksText(state.blocks),
                    modifier = Modifier.weight(1.3f)
                )
            }
        }
    }
}

@Composable
private fun ObjectiveChip(state: GameState) {
    Box(
        modifier = Modifier
            .background(Gold.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .border(1.dp, Gold.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = "☀ ${state.suns}/${Balance.TARGET_SUNS}",
            color = Charcoal,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HudChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Cream, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Column {
            Text(
                text = label,
                color = WarmGray,
                fontSize = 12.sp
            )
            Text(
                text = value,
                color = Charcoal,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun Board(
    state: GameState,
    selectedPlace: Place?,
    onPlaceSelected: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFE3CF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            Text(
                text = "TU BARRIO",
                modifier = Modifier.align(Alignment.TopCenter),
                color = WarmGray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp,
                fontSize = 12.sp
            )

            PlaceCard(
                emoji = "🛒",
                title = "Supermercado",
                subtitle = if (state.hasBike) "Comida · 0 bloques" else "Comida · 1 bloque",
                color = Sage,
                selected = selectedPlace == Place.MARKET,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.43f)
                    .clickable(enabled = state.result == GameResult.PLAYING) {
                        onPlaceSelected(Place.MARKET)
                    }
            )

            PlaceCard(
                emoji = "💼",
                title = "Trabajo",
                subtitle = "+12 € · -1 energía",
                color = SoftBlue,
                selected = selectedPlace == Place.WORK,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.43f)
                    .clickable(enabled = state.result == GameResult.PLAYING) {
                        onPlaceSelected(Place.WORK)
                    }
            )

            PlaceCard(
                emoji = if (state.hasBike) "🏠 🚲" else "🏠",
                title = "Casa",
                subtitle = if (state.hasBike) "Bici lista · tiempo para ti" else "Descanso y mejora",
                color = Terracotta,
                selected = selectedPlace == Place.HOME,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.48f)
                    .clickable(enabled = state.result == GameResult.PLAYING) {
                        onPlaceSelected(Place.HOME)
                    }
            )

            if (state.hasBike) {
                Text(
                    text = "······ 🚲 ······",
                    modifier = Modifier.align(Alignment.Center),
                    color = Terracotta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun PlaceCard(
    emoji: String,
    title: String,
    subtitle: String,
    color: Color,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.34f) else Ivory
        ),
        shape = RoundedCornerShape(22.dp),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, color)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            Text(
                text = title,
                color = Charcoal,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Text(
                text = subtitle,
                color = WarmGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ActionPanel(
    state: GameState,
    selectedPlace: Place?,
    onAction: (GameAction) -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Ivory),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.result != GameResult.PLAYING) {
                EndPanel(state, onRestart)
                return@Column
            }

            when (selectedPlace) {
                Place.WORK -> WorkPanel(state, onAction)
                Place.MARKET -> MarketPanel(state, onAction)
                Place.HOME -> HomePanel(state, onAction)
                null -> IdlePanel(state)
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color(0xFFE2D8CB))
            Text(
                text = state.message,
                color = WarmGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun IdlePanel(state: GameState) {
    Text(
        text = "¿Qué quieres hacer?",
        color = Charcoal,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp
    )
    Text(
        text = "Toca un edificio para ver sus acciones y consecuencias antes de decidir.",
        color = WarmGray,
        fontSize = 14.sp
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Objetivo de la semana",
                color = Charcoal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Consigue 7 ☀ y acaba con al menos 10 € y energía 2.",
                color = WarmGray
            )
            Text(
                text = "Esta noche: -${Balance.RENT} € de alojamiento",
                color = if (state.money < Balance.RENT) Danger else Terracotta,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WorkPanel(
    state: GameState,
    onAction: (GameAction) -> Unit
) {
    ActionHeader(
        emoji = "💼",
        title = "Trabajo",
        description = "Convierte tiempo y energía en dinero."
    )

    PreviewBox(
        lines = listOf(
            "⏱ Tiempo: ${state.blocks} → ${(state.blocks - 1).coerceAtLeast(0)} bloques",
            "💶 Dinero: ${state.money} → ${state.money + Balance.WORK_PAY} €",
            "🔋 Energía: ${state.energy} → ${(state.energy - 1).coerceAtLeast(0)}"
        )
    )

    Button(
        onClick = { onAction(GameAction.Work) },
        modifier = Modifier.fillMaxWidth(),
        enabled = state.blocks > 0 && state.energy >= Balance.WORK_ENERGY_COST,
        colors = ButtonDefaults.buttonColors(containerColor = SoftBlue)
    ) {
        Text(
            text = "Trabajar",
            color = Charcoal,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MarketPanel(
    state: GameState,
    onAction: (GameAction) -> Unit
) {
    val blockCost = if (state.hasBike) 0 else 1

    ActionHeader(
        emoji = "🛒",
        title = "Supermercado",
        description = if (state.hasBike)
            "La bici evita gastar tiempo en esta acción."
        else
            "Comer recupera energía, pero cuesta dinero y tiempo."
    )

    PreviewBox(
        lines = listOf(
            "⏱ Tiempo: ${state.blocks} → ${(state.blocks - blockCost).coerceAtLeast(0)} bloques",
            "💶 Dinero: ${state.money} → ${(state.money - Balance.FOOD_COST).coerceAtLeast(0)} €",
            "🔋 Energía: ${state.energy} → ${(state.energy + Balance.FOOD_ENERGY).coerceAtMost(Balance.MAX_ENERGY)}"
        )
    )

    Button(
        onClick = { onAction(GameAction.Eat) },
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.ateToday &&
            state.money >= Balance.FOOD_COST &&
            state.blocks >= blockCost,
        colors = ButtonDefaults.buttonColors(containerColor = Sage)
    ) {
        Text(
            text = if (state.ateToday) "Ya has comido hoy" else "Comer bien",
            color = Charcoal,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HomePanel(
    state: GameState,
    onAction: (GameAction) -> Unit
) {
    ActionHeader(
        emoji = if (state.hasBike) "🏠 🚲" else "🏠",
        title = "Casa",
        description = "Aquí eliges entre vivir el presente o invertir para ganar tiempo."
    )

    PreviewBox(
        lines = listOf(
            "Tiempo para ti",
            "⏱ -1 bloque",
            "☀ ${state.suns} → ${state.suns + 1}"
        )
    )

    Button(
        onClick = { onAction(GameAction.FreeTime) },
        modifier = Modifier.fillMaxWidth(),
        enabled = state.blocks > 0,
        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
    ) {
        Text(
            text = "Tiempo para ti · +1 ☀",
            fontWeight = FontWeight.Bold
        )
    }

    if (!state.hasBike) {
        PreviewBox(
            lines = listOf(
                "Bici de segunda mano",
                "💶 ${state.money} → ${(state.money - Balance.BIKE_COST).coerceAtLeast(0)} €",
                "Comer dejará de consumir bloques"
            )
        )

        OutlinedButton(
            onClick = { onAction(GameAction.BuyBike) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.money >= Balance.BIKE_COST
        ) {
            Text(
                text = "Comprar bici · ${Balance.BIKE_COST} €",
                color = Charcoal,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Gold.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                .padding(13.dp)
        ) {
            Text(
                text = "🚲 Bici activa: comer ya no consume tiempo.",
                color = Charcoal,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ActionHeader(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 30.sp
        )
        Column {
            Text(
                text = title,
                color = Charcoal,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
            Text(
                text = description,
                color = WarmGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun PreviewBox(lines: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream, RoundedCornerShape(16.dp))
            .padding(13.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            lines.forEachIndexed { index, line ->
                Text(
                    text = line,
                    color = if (index == 0) Charcoal else WarmGray,
                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun EndPanel(
    state: GameState,
    onRestart: () -> Unit
) {
    val won = state.result == GameResult.VICTORY

    Text(
        text = if (won) "🏆 Semana conseguida" else "Semana terminada",
        color = if (won) Charcoal else Danger,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp
    )

    Text(
        text = if (won)
            "Has protegido tu estabilidad y también tu tiempo."
        else
            "Prueba otra forma de repartir dinero, energía y tiempo.",
        color = WarmGray
    )

    PreviewBox(
        lines = listOf(
            "Resultado final",
            "☀ ${state.suns}/${Balance.TARGET_SUNS}",
            "💶 ${state.money} €",
            "🔋 ${state.energy}/${Balance.MAX_ENERGY}",
            if (state.hasBike) "🚲 Bici comprada" else "🚲 Sin bici"
        )
    )

    Button(
        onClick = onRestart,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
    ) {
        Text(
            text = "Volver a jugar",
            fontWeight = FontWeight.Bold
        )
    }
}

private fun blocksText(blocks: Int): String {
    return "●".repeat(blocks) + "○".repeat(Balance.DAILY_BLOCKS - blocks)
}
