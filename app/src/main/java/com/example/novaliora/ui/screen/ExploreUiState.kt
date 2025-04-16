package com.example.novaliora.ui.screen


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class CaptionResponse(val result: String) : UiState()
    data class Error(val e: String) : UiState()
}

data class ObjectDetectionUiData(
    val topLeft: Offset,
    val color: Color,
    val size: Size,
    val textTopLeft: Offset,
    val text: String,
)

data class SegmentationUiData(
    val data: List<SegmentUiData>,
    val colorsMap: HashMap<String,Color>
){
    data class SegmentUiData(
        val path: Path,
        val color: Color
    )
}