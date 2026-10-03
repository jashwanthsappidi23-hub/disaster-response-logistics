package com.example.daaproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.daaproject.ui.viewmodel.ScenarioViewModel

@Composable
fun AlgorithmsScreen(viewModel: ScenarioViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    var sourceId by remember { mutableStateOf("") }
    var targetId by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("100") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Algorithm Control Panel", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Shortest Path
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Dijkstra's Shortest Path", style = MaterialTheme.typography.titleLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sourceId,
                        onValueChange = { sourceId = it },
                        label = { Text("Source Node ID") },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                    OutlinedTextField(
                        value = targetId,
                        onValueChange = { targetId = it },
                        label = { Text("Target Node ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.findPath(sourceId, targetId) }) {
                    Text("Find Path")
                }
                uiState.algorithmResults.shortestPath?.let { result ->
                    Text("Path: ${result.path.joinToString(" -> ")}")
                    Text("Total Distance: ${result.distance}")
                }
            }
        }

        // MST
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Minimum Spanning Tree (Kruskal/Prim)", style = MaterialTheme.typography.titleLarge)
                uiState.algorithmResults.mst?.let { mst ->
                    Text("Kruskal's MST Edges: ${mst.size}")
                    val totalWeight = mst.map { it.distance }.sum()
                    Text("Total Weight: $totalWeight")
                }
                Spacer(modifier = Modifier.height(8.dp))
                uiState.algorithmResults.primMst?.let { primMst ->
                    Text("Prim's MST Edges: ${primMst.size}")
                    val totalWeight = primMst.map { it.distance }.sum()
                    Text("Total Weight: $totalWeight")
                }
            }
        }

        // Knapsack
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Resource Allocation (Knapsack 0/1)", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Vehicle Capacity") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.setKnapsackCapacity(capacity.toIntOrNull() ?: 100) }) {
                    Text("Run Knapsack")
                }
                uiState.algorithmResults.knapsackResult?.let { result ->
                    Text("Max Value: ${result.maxValue}")
                    Text("Selected Items: ${result.selectedResources.map { it.type }.joinToString(", ")}")
                }
            }
        }
        
        // Greedy Allocation
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Greedy Demand Allocation", style = MaterialTheme.typography.titleLarge)
                uiState.algorithmResults.resourceAllocations?.let { allocs ->
                    allocs.forEach { alloc ->
                        Text("Demand ${alloc.demandId} -> Allocated ${alloc.amountAllocated} items")
                    }
                }
            }
        }

        // Red Black Tree
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sorted Demands (Red-Black Tree)", style = MaterialTheme.typography.titleLarge)
                uiState.algorithmResults.rbtSortedDemands?.let { demands ->
                    Text(demands.joinToString(", ") { "Demand ${it.id} (Pop: ${it.population})" })
                }
            }
        }
    }
}
