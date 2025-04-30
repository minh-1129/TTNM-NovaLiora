package com.example.novaliora.ui.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.compiler.plugins.kotlin.EmptyFunctionMetrics.composable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.novaliora.features.object_detection.YuvToRgbConverter
import com.example.novaliora.ui.screen.CameraPermission
import com.example.novaliora.ui.screen.DetectionScreen
import com.example.novaliora.ui.screen.ExploreScreen
import com.example.novaliora.ui.screen.FaceRecognitionScreen
import com.example.novaliora.ui.screen.MoodTrackingScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.tensorflow.lite.Interpreter
import java.util.concurrent.ExecutorService
import com.example.novaliora.ui.screen.TextRecognitionScreen


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ApplicationNavHost(
    navController: NavHostController,
    cameraExecutor: ExecutorService,
    yuvToRgbConverter: YuvToRgbConverter,
    interpreter: Interpreter,
    labels: List<String>,
    textToSpeech: TextToSpeech,
    modifier: Modifier = Modifier
) {

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
    } else {
        CameraPermission(cameraPermissionState)
    }

    NavHost(
        navController = navController,
        startDestination = DetectionDestination.route,
        modifier = modifier
    ) {
        composable(route = DetectionDestination.route) {
            DetectionScreen(cameraExecutor = cameraExecutor,
                yuvToRgbConverter = yuvToRgbConverter,
                interpreter = interpreter,
                labels = labels,
                textToSpeech = textToSpeech,
                navigateToRight = {navController.navigate(ExploreDestination.route)},
                navigateToLeft = {navController.navigate(FaceRecognition.route)}
            )
        }
        composable(route = ExploreDestination.route) {
            ExploreScreen(
                navigateToLeft = {navController.navigate(DetectionDestination.route)},
                navigateToRight = {navController.navigate(MoodTrackingDestination.route)}
            )

        }
        composable(route = MoodTrackingDestination.route) {
            MoodTrackingScreen(cameraExecutor = cameraExecutor,
                navigateToRight = {navController.navigate(FaceRecognition.route)},
                navigateToLeft = {navController.navigate(ExploreDestination.route)}
            )
        }
        composable(route = FaceRecognition.route) {
            FaceRecognitionScreen(
                cameraExecutor = cameraExecutor,
                navigateToRight = {navController.navigate(TextRecognition.route)},
                navigateToLeft = {navController.navigate(MoodTrackingDestination.route)}
            )

        }


        composable(route = FaceRecognition.route) {
            TextRecognitionScreen(
                cameraExecutor = cameraExecutor,
                navigateToRight = {navController.navigate(DetectionDestination.route)},
                navigateToLeft = {navController.navigate(FaceRecognition.route)}
            )

        }
    }
}