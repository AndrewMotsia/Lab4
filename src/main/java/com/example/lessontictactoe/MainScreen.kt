package com.example.lessontictactoe

import android.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lessontictactoe.ui.theme.LessonTicTacToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Перерахування (enum) для гри
enum class GameState {
    IN_PROGRESS,
    CROSS_WIN,
    NOUGHT_WIN,
    DRAW
}

enum class Player {
    CROSS,
    NOUGHT
}

enum class CellState {
    EMPTY,
    CROSS,
    NOUGHT
}

// Перетворює гравця на його позначку в грі (хрестик або нолик)
val Player.mark: CellState
    get() = when (this) {
        Player.CROSS -> CellState.CROSS
        Player.NOUGHT -> CellState.NOUGHT
    }

// Перевірка перемоги в рядках для гри на полі
fun checkGameState(field: List<CellState>, dim: Int): GameState {
    for (row in 0 until dim) {
        for (col in 0 until dim - 2) {
            val startIndex = row * dim + col
            if (field[startIndex] != CellState.EMPTY &&
                field[startIndex] == field[startIndex + 1] &&
                field[startIndex + 1] == field[startIndex + 2]
            ) {
                return when (field[startIndex]) {
                    CellState.CROSS -> GameState.CROSS_WIN
                    CellState.NOUGHT -> GameState.NOUGHT_WIN
                    else -> GameState.IN_PROGRESS
                }
            }
        }
    }

    for (col in 0 until dim) {
        for (row in 0 until dim - 2) {
            val startIndex = row * dim + col
            if (field[startIndex] != CellState.EMPTY &&
                field[startIndex] == field[startIndex + dim] &&
                field[startIndex + dim] == field[startIndex + 2 * dim]
            ) {
                return when (field[startIndex]) {
                    CellState.CROSS -> GameState.CROSS_WIN
                    CellState.NOUGHT -> GameState.NOUGHT_WIN
                    else -> GameState.IN_PROGRESS
                }
            }
        }
    }

    for (row in 0 until dim - 2) {
        for (col in 0 until dim - 2) {
            val startIndex = row * dim + col
            if (field[startIndex] != CellState.EMPTY &&
                field[startIndex] == field[startIndex + dim + 1] &&
                field[startIndex + dim + 1] == field[startIndex + 2 * (dim + 1)]
            ) {
                return when (field[startIndex]) {
                    CellState.CROSS -> GameState.CROSS_WIN
                    CellState.NOUGHT -> GameState.NOUGHT_WIN
                    else -> GameState.IN_PROGRESS
                }
            }
        }
    }

    for (row in 0 until dim - 2) {
        for (col in 2 until dim) {
            val startIndex = row * dim + col
            if (field[startIndex] != CellState.EMPTY &&
                field[startIndex] == field[startIndex + dim - 1] &&
                field[startIndex + dim - 1] == field[startIndex + 2 * (dim - 1)]
            ) {
                return when (field[startIndex]) {
                    CellState.CROSS -> GameState.CROSS_WIN
                    CellState.NOUGHT -> GameState.NOUGHT_WIN
                    else -> GameState.IN_PROGRESS
                }
            }
        }
    }

    return if (field.any { it == CellState.EMPTY }) {
        GameState.IN_PROGRESS
    } else {
        GameState.DRAW
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedDim by remember { mutableStateOf<Int?>(null) }
    var crossWins by remember { mutableStateOf(0) }
    var noughtWins by remember { mutableStateOf(0) }
    var showScore by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf(false) }

    LessonTicTacToeTheme(darkTheme = isDarkTheme) {
        if (selectedDim == null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tic Tac Toe",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF006400),
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Theme",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF2E8B57),
                            uncheckedThumbColor = Color(0xFF2E8B57),
                            checkedTrackColor = Color(0x99FFFFFF),
                            uncheckedTrackColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                Text(
                    text = "Choose board size:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                listOf(3, 4, 5).forEach { dim ->
                    Button(
                        onClick = { selectedDim = dim },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E8B57),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "${dim}x$dim",
                            fontSize = 20.sp
                        )
                    }
                }

            }
        } else {
            GameScreen(
                dim = selectedDim!!,
                crossWins = crossWins,
                noughtWins = noughtWins,
                onCrossWin = { crossWins++ },
                onNoughtWin = { noughtWins++ },
                onNewGame = {
                    selectedDim = null
                    crossWins = 0
                    noughtWins = 0
                    showScore = false
                },
                showScore = showScore,
                toggleScore = { showScore = !showScore }
            )
        }
    }
}

@Composable
fun GameScreen(
    dim: Int,
    crossWins: Int,
    noughtWins: Int,
    onCrossWin: () -> Unit,
    onNoughtWin: () -> Unit,
    onNewGame: () -> Unit,
    showScore: Boolean,
    toggleScore: () -> Unit
) {
    val field = remember { mutableStateListOf(*Array(dim * dim) { CellState.EMPTY }) }
    var currentPlayer by remember { mutableStateOf(Player.CROSS) }
    var gameState by remember { mutableStateOf(GameState.IN_PROGRESS) }
    var timeLeft by remember { mutableStateOf(10) }
    val coroutineScope = rememberCoroutineScope()

    val cellSize = when (dim) {
        3 -> 100.dp
        4 -> 80.dp
        5 -> 60.dp
        else -> 60.dp
    }

    LaunchedEffect(currentPlayer, gameState) {
        if (gameState == GameState.IN_PROGRESS) {
            timeLeft = 10
            while (timeLeft > 0 && gameState == GameState.IN_PROGRESS) {
                delay(1000L)
                timeLeft--
            }
            if (timeLeft == 0 && gameState == GameState.IN_PROGRESS) {
                currentPlayer = if (currentPlayer == Player.CROSS) Player.NOUGHT else Player.CROSS
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tic Tac Toe",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF006400),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Time: $timeLeft s",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            color = if (timeLeft <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = when (gameState) {
                GameState.CROSS_WIN -> "X wins!"
                GameState.NOUGHT_WIN -> "O wins!"
                GameState.DRAW -> "Draw !"
                GameState.IN_PROGRESS -> "Player's turn ${if (currentPlayer == Player.CROSS) "X" else "O"}"
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF556B2F),
            fontWeight = FontWeight.Bold
        )

        if (showScore) {
            Text(
                text = "Score: X - $crossWins, O - $noughtWins",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold

            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until dim) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (col in 0 until dim) {
                        val index = row * dim + col
                        val cellColor by animateColorAsState(
                            targetValue = if (field[index] == CellState.EMPTY) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                Color(0x9937A768)
                            }
                        )

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .padding(4.dp)
                                .background(cellColor)
                                .border(2.dp, MaterialTheme.colorScheme.primary)
                                .clickable(enabled = gameState == GameState.IN_PROGRESS) {
                                    if (field[index] == CellState.EMPTY) {
                                        field[index] = currentPlayer.mark
                                        gameState = checkGameState(field, dim)
                                        when (gameState) {
                                            GameState.CROSS_WIN -> onCrossWin()
                                            GameState.NOUGHT_WIN -> onNoughtWin()
                                            else -> {}
                                        }
                                        if (gameState == GameState.IN_PROGRESS) {
                                            currentPlayer = if (currentPlayer == Player.CROSS) Player.NOUGHT else Player.CROSS
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (field[index]) {
                                    CellState.EMPTY -> ""
                                    CellState.CROSS -> "X"
                                    CellState.NOUGHT -> "O"
                                },
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    field.clear()
                    field.addAll(Array(dim * dim) { CellState.EMPTY }.toList())
                    currentPlayer = Player.CROSS
                    gameState = GameState.IN_PROGRESS
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006400),
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Reset Round")
            }

            Button(
                onClick = toggleScore,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006400),
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(if (showScore) " Hide Score" else "Show Score")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNewGame,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E8B57),
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("New Game")
        }

        if (gameState != GameState.IN_PROGRESS) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    field.clear()
                    field.addAll(Array(dim * dim) { CellState.EMPTY }.toList())
                    currentPlayer = Player.CROSS
                    gameState = GameState.IN_PROGRESS
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E8B57),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Play Again")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LessonTicTacToeTheme(darkTheme = true) {
        MainScreen()
    }
}