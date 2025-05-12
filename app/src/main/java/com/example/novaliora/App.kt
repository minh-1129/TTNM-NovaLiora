package com.example.novaliora

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.novaliora.features.object_detection.YuvToRgbConverter
import com.example.novaliora.ui.navigation.ApplicationNavHost
import org.tensorflow.lite.Interpreter
import java.util.concurrent.ExecutorService

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun AppBar(
    destinationName: String,
    modifier: Modifier = Modifier,
) {
    val selectedTabIndex = when (destinationName) {
        stringResource(R.string.detection) -> 0
        stringResource(R.string.explore) -> 1
        stringResource(R.string.mood_tracking) -> 2
        stringResource(R.string.face_recognition) -> 3
        stringResource(R.string.text_recognition) -> 4
        else -> 0
    }

    val tabs = listOf(
        TabItem("DETECT", Icons.Filled.ImageSearch),
        TabItem("EXPLORE", Icons.Filled.Explore),
        TabItem("EMOTION", Icons.Filled.EmojiEmotions),
        TabItem("FACE", Icons.Filled.Face),
        TabItem("Text", Icons.Filled.Textsms)
    )

    Surface(
        elevation = 6.dp,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        color = Color(0xFFF8F8F8),
        modifier = modifier.fillMaxWidth()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Transparent,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .padding(horizontal = 12.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Black)
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, tabItem ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { /**/ },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedTabIndex == index) Color.White else Color.Transparent
                        ),
                    text = {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = tabItem.title,
                                tint = if (selectedTabIndex == index) Color.Black else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = tabItem.title,
                                color = if (selectedTabIndex == index) Color.Black else Color.Gray,
                                fontSize = 10.sp,
                                style = MaterialTheme.typography.caption.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun App(navHostController: NavHostController = rememberNavController(), cameraExecutor: ExecutorService, yuvToRgbConverter: YuvToRgbConverter, interpreter: Interpreter, labels: List<String>, textToSpeech: TextToSpeech) {
    ApplicationNavHost(navController = navHostController, cameraExecutor = cameraExecutor, yuvToRgbConverter = yuvToRgbConverter, interpreter = interpreter, labels = labels, textToSpeech = textToSpeech)
}