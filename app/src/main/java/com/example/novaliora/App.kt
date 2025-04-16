package com.example.novaliora


import android.speech.tts.TextToSpeech
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.novaliora.ui.screen.ExploreScreen
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
import com.example.novaliora.features.object_detection.YuvToRgbConverter
import com.example.novaliora.ui.screen.CameraPermission
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.accompanist.pager.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.tensorflow.lite.Interpreter
import java.util.concurrent.ExecutorService
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalPermissionsApi::class)
@Composable
fun App(cameraExecutor: ExecutorService, yuvToRgbConverter: YuvToRgbConverter, interpreter: Interpreter, labels: List<String>, textToSpeech: TextToSpeech) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
    } else {
        CameraPermission(cameraPermissionState)
    }

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val screens = listOf(
        Screen("Detection", Icons.Default.Search),
        Screen("Explore", Icons.Default.Warning),
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
                0 -> DetectionScreen(cameraExecutor = cameraExecutor,
                    yuvToRgbConverter = yuvToRgbConverter,
                    interpreter = interpreter,
                    labels = labels,
                    textToSpeech = textToSpeech)
                1 -> ExploreScreen()
                2 -> MoodTrackingScreen(cameraExecutor = cameraExecutor)
                3 -> FaceRecognitionScreen(cameraExecutor = cameraExecutor)
            }
        }
    }
}

data class Screen(val title: String, val icon: ImageVector)
