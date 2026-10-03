package com.example.daaproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.daaproject.ui.viewmodel.ScenarioViewModel

@Composable
fun EocDashboardScreen(viewModel: ScenarioViewModel) {
    var selectedTab by remember { mutableStateOf("Dashboard") }

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.width(100.dp)
        ) {
            NavigationRailItem(
                selected = selectedTab == "Dashboard",
                onClick = { selectedTab = "Dashboard" },
                icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                label = { Text("KPIs") }
            )
            NavigationRailItem(
                selected = selectedTab == "Map",
                onClick = { selectedTab = "Map" },
                icon = { Icon(Icons.Default.Place, contentDescription = "Map") },
                label = { Text("Map") }
            )
            NavigationRailItem(
                selected = selectedTab == "Algorithms",
                onClick = { selectedTab = "Algorithms" },
                icon = { Icon(Icons.Default.Build, contentDescription = "Algorithms") },
                label = { Text("Algorithms") }
            )
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTab) {
                "Dashboard" -> KpiDashboardScreen(viewModel)
                "Map" -> NetworkMapScreen(viewModel)
                "Algorithms" -> AlgorithmsScreen(viewModel)
            }
        }
    }
}
