package com.example.novaliora.ui.screen

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.Surface
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.novaliora.AppBar
import com.example.novaliora.DragThreshold
import com.example.novaliora.R
import com.example.novaliora.presentation.MainViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import java.util.concurrent.ExecutorService
import kotlin.math.abs
import com.example.novaliora.DragThreshold
import kotlinx.coroutines.delay


@OptIn(ExperimentalGetImage::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TextRecognitionScreen(
    cameraExecutor: ExecutorService,
    viewModel: MainViewModel = hiltViewModel(),
    navigateToLeft: () -> Unit = {},
    navigateToRight: () -> Unit = {}
) {
    val context = LocalContext.current
    val detectionSound = remember { MediaPlayer.create(context, R.raw.text_detection) }

    val lifecycleOwner = LocalLifecycleOwner.current
    lateinit var previewView: PreviewView

    val recognizedText = remember { mutableStateOf("Chưa nhận diện được văn bản") }

    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }

    var lastSpokenText = remember { mutableStateOf("") }
    val isReading = remember { mutableStateOf(false) }

    // Khởi tạo TextToSpeech
    LaunchedEffect(Unit) {
        detectionSound.start()
        delay(detectionSound.duration.toLong())
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d("TTS", "onStart called with id: $utteranceId")
                    }

                    override fun onDone(utteranceId: String?) {
                        Log.d("TTS", "onDone called with id: $utteranceId")
                        isReading.value = false // Cho phép nhận diện lại
                        lastSpokenText.value = ""
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e("TTS", "onError called with id: $utteranceId")
                        Log.d("TTS", isReading.value.toString())
                        isReading.value = false
                    }
                })
                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TEST_ID")
                }
                textToSpeech?.speak("Hello world", TextToSpeech.QUEUE_FLUSH, params, "TEST_ID")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            detectionSound.stop()
            detectionSound.release()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
//            viewModel.stopCamera()
        }
    }

    // ML Kit Text Recognizer
    val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    var lastSpeakTime = remember { mutableStateOf(0L) }
    val speakDelayMillis = 2000L // 2 giây

    val analyzer = ImageAnalysis.Analyzer { imageProxy ->
        if (isReading.value) {
            imageProxy.close()
            return@Analyzer
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val detectedText = visionText.text
                    Log.d("TTS", detectedText)
                    val currentTime = SystemClock.elapsedRealtime()

                    if (
                        detectedText.isNotBlank() &&
                        detectedText != lastSpokenText.value
//                        && currentTime - lastSpeakTime.value > speakDelayMillis
                    ) {
                        lastSpokenText.value = detectedText
                        recognizedText.value = detectedText
                        lastSpeakTime.value = currentTime
                        isReading.value = true
                        Log.d("TTS", isReading.value.toString())
//                        val params = Bundle().apply {
//                            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TTS_DONE")
//                        }
                        textToSpeech?.speak(detectedText, TextToSpeech.QUEUE_FLUSH, null, "TTS_DONE")                    }
                }
                .addOnFailureListener {
                    recognizedText.value = "Không thể nhận diện văn bản"
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }


    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetRotation(Surface.ROTATION_0)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(cameraExecutor, analyzer)
        }

    viewModel.initRepo(imageAnalysis)

    var hasNavigated by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { hasNavigated = false },
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
        bottomBar = {
            AppBar(destinationName = stringResource(R.string.text_recognition))
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    previewView = PreviewView(it)
                    viewModel.showCameraPreview(previewView, lifecycleOwner)
                    previewView
                },
                modifier = Modifier.fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                textToSpeech?.speak(recognizedText.value, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        )
                    }
            )

            Text(
                text = recognizedText.value,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            )
        }
    }
}


