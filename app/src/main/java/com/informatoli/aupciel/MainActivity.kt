package com.informatoli.aupciel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.informatoli.aupciel.ui.game.UrbanCozyGameScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UrbanCozyGameScreen()
        }
    }
}
