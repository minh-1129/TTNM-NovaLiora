package com.example.novaliora


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.novaliora.ui.screen.DangerWarningScreen
import com.example.novaliora.ui.screen.DetectionScreen
import com.example.novaliora.ui.screen.FaceRecognitionScreen
import com.example.novaliora.ui.screen.MoodTrackingScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun App() {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val screens = listOf(
        Screen("Detection", Icons.Default.Search),
        Screen("Danger", Icons.Default.Warning),
        Screen("Mood", Icons.Default.CheckCircle),
        Screen("Face", Icons.Default.Face)
    )

    Scaffold(
        bottomBar = {
            BottomNavigation {
                screens.forEachIndexed { index, screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            count = screens.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> DetectionScreen()
                1 -> DangerWarningScreen()
                2 -> MoodTrackingScreen()
                3 -> FaceRecognitionScreen()
            }
        }
    }
}

data class Screen(val title: String, val icon: ImageVector)
