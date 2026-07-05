package com.litetodo.app.ui

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.litetodo.app.ui.home.HomeScreen
import com.litetodo.app.ui.settings.SettingsScreen
import com.litetodo.app.ui.theme.DarkModeOption
import com.litetodo.app.ui.theme.LiteToDoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

@Composable
fun LiteToDoApp() {
    val context = LocalContext.current
    var darkMode by remember { mutableStateOf(DarkModeOption.SYSTEM) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("dark_mode")]
        }.collect { value ->
            darkMode = when (value) {
                "dark" -> DarkModeOption.DARK
                "light" -> DarkModeOption.LIGHT
                else -> DarkModeOption.SYSTEM
            }
        }
    }

    LiteToDoTheme(darkMode = darkMode) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("settings") {
                SettingsScreen(
                    darkMode = darkMode,
                    onDarkModeChange = { mode ->
                        darkMode = mode
                        scope.launch {
                            context.dataStore.edit { prefs ->
                                prefs[stringPreferencesKey("dark_mode")] = when (mode) {
                                    DarkModeOption.DARK -> "dark"
                                    DarkModeOption.LIGHT -> "light"
                                    DarkModeOption.SYSTEM -> "system"
                                }
                            }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
