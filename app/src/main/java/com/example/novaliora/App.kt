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
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import com.google.accompanist.pager.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun App() {
    val pagerState = rememberPagerState()
    val screens = listOf(
        R.string.detection, R.string.danger_warning, R.string.mood_tracking, R.string.face_recognition
    )

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = screens.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> DetectionScreen()
                1 -> DangerWarningScreen()
                2 -> MoodTrackingScreen()
                3 -> FaceRecognitionScreen()
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )

        Text(
            text = stringResource(id = screens[pagerState.currentPage]),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
