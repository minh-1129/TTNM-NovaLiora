package com.example.novaliora.ui.screen


import android.annotation.SuppressLint
import android.graphics.PointF
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.novaliora.AppBar
import com.example.novaliora.ApplicationViewModel
import com.example.novaliora.DragThreshold
import com.example.novaliora.R
import com.example.novaliora.features.face_recognition.FaceNetModel
import com.example.novaliora.features.face_recognition.FaceRecognitionAnalyzer
import com.example.novaliora.presentation.MainViewModel
import com.example.novaliora.ui.navigation.FaceRecognition
import com.example.novaliora.utils.adjustPoint
import com.example.novaliora.utils.adjustSize
import com.example.novaliora.utils.drawBounds
import com.google.mlkit.vision.face.Face
import java.util.Locale
import java.util.concurrent.ExecutorService
import kotlin.collections.forEach
import kotlin.math.abs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FaceRecognitionScreen(
    cameraExecutor: ExecutorService,
    viewModel: MainViewModel = hiltViewModel(),
    navigateToLeft: () -> Unit = {},
    navigateToRight: () -> Unit = {},

) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    lateinit var previewView: PreviewView

    val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

    val imageWidth = remember { mutableStateOf(0) }
    val imageHeight = remember { mutableStateOf(0) }

    val faces = remember { mutableStateListOf<Face>() }

    val recognizedPerson = remember { mutableStateOf("None") }
    val distance = remember { mutableStateOf(0f) }

    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    val recognitionSound = remember { MediaPlayer.create(context, R.raw.face_recognition) }

    LaunchedEffect(Unit) {
        recognitionSound.start()
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
            }
        }
    }

    val applicationViewModel: ApplicationViewModel = hiltViewModel()
    val faceNetModel: FaceNetModel = applicationViewModel.faceNetModel

    DisposableEffect(Unit) {
        onDispose {
            recognitionSound.stop()
            recognitionSound.release()
            textToSpeech?.shutdown()
        }
    }

    val faceRecognitionAnalyzer = FaceRecognitionAnalyzer(context, faceNetModel) { detectedFace, width, height, name, actualDistance  ->
        faces.clear()
        faces.addAll(detectedFace)
        imageWidth.value = width
        imageHeight.value = height
        recognizedPerson.value = name
        distance.value = actualDistance
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetRotation(android.view.Surface.ROTATION_0)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(cameraExecutor, faceRecognitionAnalyzer)
        }

    viewModel.initRepo(imageAnalysis)
    var hasNavigated by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    hasNavigated = false // Reset khi bắt đầu kéo
                },
                onDrag = { change, dragAmount ->
                    if (!hasNavigated && abs(dragAmount.x) > abs(dragAmount.y)) {
                        if (abs(dragAmount.x) > DragThreshold) {
                            hasNavigated = true
                            if (dragAmount.x > 0) {
                                navigateToLeft()
                            } else {
                                navigateToRight()
                            }
                        }
                    }
                }
            )
        },
        bottomBar  = {
            AppBar(destinationName = stringResource(FaceRecognition.titleRes))
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    previewView = PreviewView(it)
                    viewModel.showCameraPreview(previewView = previewView, lifecycleOwner = lifecycleOwner)
                    previewView
                },
                modifier = Modifier.fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                textToSpeech?.speak(recognizedPerson.value, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        )
                    }
            )
            DrawFaces(faces, imageHeight.value, imageWidth.value, screenWidth.value, screenHeight.value, recognizedPerson.value, distance.value)
        }
    }
}

@Composable
fun DrawFaces(faces: List<Face>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int, name: String, distance: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        faces.forEach { face ->
            val boundingBox = face.boundingBox.toComposeRect()
            val topLeft = adjustPoint(PointF(boundingBox.left, boundingBox.top), imageWidth, imageHeight, screenWidth, screenHeight)
            val size = adjustSize(
                boundingBox.size,
                imageWidth, imageHeight, screenWidth, screenHeight
            )
            drawBounds(topLeft, size, Color.Yellow, 5f)

            val recognition = "$name\n$distance"
            drawContext.canvas.nativeCanvas.drawText(
                recognition,
                topLeft.x,
                topLeft.y - 10,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.YELLOW
                    textSize = 40f
                }
            )
        }
    }
}
