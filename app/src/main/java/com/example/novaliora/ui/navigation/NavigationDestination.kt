package com.example.novaliora.ui.navigation

import com.example.novaliora.R

interface NavigationDestination {
    val route: String
    val titleRes: Int
}

object DetectionDestination : NavigationDestination {
    override val route = "detection"
    override val titleRes = R.string.detection
}

object ExploreDestination: NavigationDestination {
    override val route = "explore"
    override val titleRes = R.string.explore
}

object MoodTrackingDestination: NavigationDestination {
    override val route = "tracking"
    override val titleRes = R.string.mood_tracking
}

object FaceRecognition: NavigationDestination {
    override val route = "face_recognition"
    override val titleRes = R.string.face_recognition
}

object TextRecognition: NavigationDestination {
    override val route = "text_recognition"
    override val titleRes = R.string.text_recognition
}
