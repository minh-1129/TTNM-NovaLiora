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
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import com.example.novaliora.features.object_detection.YuvToRgbConverter
import com.example.novaliora.ui.screen.CameraPermission
import com.google.accompanist.pager.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.tensorflow.lite.Interpreter
import java.util.concurrent.ExecutorService

@OptIn(ExperimentalPagerApi::class, ExperimentalPermissionsApi::class)
@Composable
fun App(cameraExecutor: ExecutorService, yuvToRgbConverter: YuvToRgbConverter, interpreter: Interpreter, labels: List<String>, textToSpeech: TextToSpeech) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
    } else {
        CameraPermission(cameraPermissionState)
    }

    val pagerState = rememberPagerState()
    val screens = listOf(
        R.string.detection, R.string.explore, R.string.mood_tracking, R.string.face_recognition
    )

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = screens.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
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
