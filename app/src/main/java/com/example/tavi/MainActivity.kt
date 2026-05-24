package com.example.tavi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.tavi.shell.TaviShellScreen
import com.example.tavi.ui.theme.TaviTheme
import com.example.tavi.viewmodel.TaviViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TaviViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Swallow back button — launcher must not exit
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = Unit
        })

        setContent {
            TaviTheme {
                val uiState by viewModel.state.collectAsState()
                TaviShellScreen(uiState = uiState, viewModel = viewModel, warden = viewModel.warden)
            }
        }
    }
}
