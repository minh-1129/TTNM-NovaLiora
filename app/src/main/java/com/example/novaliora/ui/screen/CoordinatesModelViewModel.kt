package com.example.novaliora.ui.screen


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.novaliora.Data.CoordinatesModelRepo
import com.example.novaliora.Data.RequestModel
import kotlinx.coroutines.launch
import java.util.Locale

class CoordinatesModelViewModel(
    private val coordinatesModelRepo: CoordinatesModelRepo
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> get() = _uiState

    var imageLeftDistance by mutableFloatStateOf(0.0f)

    init {
        resetState()
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    fun getCoordinatesModel(requestModel: RequestModel) {
        _uiState.value = UiState.Loading
        Log.d("ExploreScreen", "Coordinate - Loading")

        viewModelScope.launch {
            try {
                val coordinatesModel = coordinatesModelRepo
                    .getCoordinatesModel(requestModel)
                    .body()

                _uiState.value = when {

                    coordinatesModel?.response != null -> {
                        UiState.CaptionResponse(coordinatesModel.response)
                    }

                    coordinatesModel?.error != null -> {
                        UiState.Error(
                            coordinatesModel.error
                        )
                    }

                    else -> {
                        UiState.Error("No result found.")
                    }
                }

                Log.d("ExploreScreen", "Coordinate - ${_uiState.value.toString()}")

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = if (e.message != null) {
                    UiState.Error(e.message!!)
                } else {
                    UiState.Error("An unknown error occurred.")
                }
            }
        }
    }

}

class CoordinatesModelViewModelFactory(private val coordinatesModelRepo: CoordinatesModelRepo) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CoordinatesModelViewModel(coordinatesModelRepo) as T
    }
}

private fun String.filterLabel(): String {
    return this.replace("'", "").trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
}

private fun getRandomColor(): Color {
    val r = (1..254).random()
    val g = (1..254).random()
    val b = (1..254).random()
    return Color(
        red = r,
        green = g,
        blue = b
    )
}