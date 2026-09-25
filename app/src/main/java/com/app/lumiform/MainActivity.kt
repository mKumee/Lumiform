package com.app.lumiform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.uicomponents.theme.LumiformTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.lumiform.navigation.AppNavHost

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            LumiformTheme(themeMode = themeMode) {
                AppNavHost(themeMode = themeMode, onToggleTheme = { viewModel.switchTheme() }, modifier = Modifier.fillMaxSize())
            }
        }
    }
}