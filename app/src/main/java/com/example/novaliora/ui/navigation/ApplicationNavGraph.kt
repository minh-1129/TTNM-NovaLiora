//package com.example.novaliora.ui.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.novaliora.ui.screen.DetectionScreen
//import com.example.novaliora.ui.screen.DangerWarningScreen
//
//@Composable
//fun ApplicationNavGraph(
//    navController: NavHostController,
//    startDestination: String = DetectionDestination.route
//) {
//    NavHost(
//        navController = navController,
//        startDestination = startDestination
//    ) {
//        composable(DetectionDestination.route) {
//            DetectionScreen()
//        }
//        composable(DangerWarningDestination.route) {
//            DangerWarningScreen()
//        }
//
//    }
//}