// MainActivity.kt
package com.example.novaliora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novaliora.ui.navigation.ApplicationNavGraph
import com.example.novaliora.ui.navigation.DetectionDestination
import com.example.novaliora.ui.theme.NovaLioraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NovaLioraTheme {
                val navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    var currentDestination by remember { mutableStateOf(DetectionDestination.route) }

    Column {
        AppBar(currentDestination = currentDestination) { selectedRoute ->
            currentDestination = selectedRoute
            navController.navigate(selectedRoute) {
                // Clear the back stack to avoid multiple back-stack entries
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }

        ApplicationNavGraph(navController = navController, startDestination = currentDestination)
    }
}