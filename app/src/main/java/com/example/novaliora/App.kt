package com.example.novaliora

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.novaliora.ui.navigation.DangerWarningDestination
import com.example.novaliora.ui.navigation.DetectionDestination
import com.example.novaliora.ui.navigation.ExploreDestination

@Composable
fun AppBar(
    currentDestination: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        stringResource(R.string.detection) to DetectionDestination.route,
        stringResource(R.string.danger_warning) to DangerWarningDestination.route,
        stringResource(R.string.explore) to ExploreDestination.route
    )

    val selectedTabIndex = tabs.indexOfFirst { it.first == currentDestination }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Color.Black,
                height = 4.dp
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(tab.second) },
                text = { Text(tab.first) }
            )
        }
    }
}
