
package com.example.novaliora.domain

import android.content.Context
import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

interface CustomCameraRepo {
    suspend fun captureAndSaveImage(context: Context, onImageCaptured: (Uri) -> Unit = {})
    suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    )
}