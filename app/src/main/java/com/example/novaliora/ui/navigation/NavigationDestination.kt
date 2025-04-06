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

object DangerWarningDestination : NavigationDestination {
    override val route = "danger_warning"
    override val titleRes = R.string.danger_warning
}

object ExploreDestination: NavigationDestination {
    override val route = "explore"
    override val titleRes = R.string.explore
}