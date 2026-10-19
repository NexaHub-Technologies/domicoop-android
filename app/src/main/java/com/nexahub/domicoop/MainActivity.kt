package com.nexahub.domicoop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nexahub.domicoop.navigation.AppNavGraph
import com.nexahub.domicoop.theme.DOMICOOPTheme
import com.nexahub.domicoop.theme.ThemePreference
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = (application as DomiCoopApp).container
        enableEdgeToEdge()
        setContent {
            var pref by remember { mutableStateOf(ThemePreference.SYSTEM) }
            val scope = rememberCoroutineScope()
            androidx.compose.runtime.LaunchedEffect(Unit) {
                try {
                    pref = when (app.session.getTheme()) {
                        "light" -> ThemePreference.LIGHT
                        "dark" -> ThemePreference.DARK
                        else -> ThemePreference.SYSTEM
                    }
                } catch (_: Exception) { }
            }
            DOMICOOPTheme(preference = pref) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavGraph(
                        app = app,
                        themePref = pref,
                        onThemeChange = { next ->
                            pref = next
                            scope.launch {
                                try {
                                    app.session.setTheme(
                                        when (next) {
                                            ThemePreference.LIGHT -> "light"
                                            ThemePreference.DARK -> "dark"
                                            ThemePreference.SYSTEM -> "system"
                                        },
                                    )
                                } catch (_: Exception) { }
                            }
                        },
                    )
                }
            }
        }
    }
}
