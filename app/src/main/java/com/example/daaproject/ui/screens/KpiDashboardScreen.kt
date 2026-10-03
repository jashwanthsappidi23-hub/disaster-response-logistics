package com.example.daaproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.daaproject.ui.viewmodel.ScenarioViewModel

@Composable
fun KpiDashboardScreen(viewModel: ScenarioViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("KPI Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            KpiCard("Total Affected", uiState.metrics.totalAffectedPeople.toString())
            KpiCard("Active Depots", uiState.metrics.activeDepots.toString())
            KpiCard("Total Resources", uiState.metrics.totalResourcesAvailable.toString())
            KpiCard("Demand Satisfaction", "${"%.1f".format(uiState.metrics.demandSatisfactionPercent)}%")
        }
    }
}

@Composable
fun KpiCard(title: String, value: String) {
    Card(
        modifier = Modifier.size(160.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
