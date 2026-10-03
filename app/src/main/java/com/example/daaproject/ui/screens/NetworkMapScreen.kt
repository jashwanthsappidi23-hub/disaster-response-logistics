package com.example.daaproject.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.daaproject.data.entity.Edge
import com.example.daaproject.data.entity.Node
import com.example.daaproject.ui.viewmodel.ScenarioViewModel

@Composable
fun NetworkMapScreen(viewModel: ScenarioViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val nodes = uiState.nodes
    val edges = uiState.edges
    
    // Support pan and zoom
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Interactive Network Map", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
        
        if (nodes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No data available to display map.")
            }
            return
        }

        val minLat = nodes.minByOrNull { it.lat }?.lat ?: 0.0
        val maxLat = nodes.maxByOrNull { it.lat }?.lat ?: 0.0
        val minLng = nodes.minByOrNull { it.lng }?.lng ?: 0.0
        val maxLng = nodes.maxByOrNull { it.lng }?.lng ?: 0.0

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 5f)
                        offset += pan
                    }
                }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                fun getOffset(node: Node): Offset {
                    val x = if (maxLng == minLng) canvasWidth / 2 else ((node.lng - minLng) / (maxLng - minLng)) * canvasWidth
                    val y = if (maxLat == minLat) canvasHeight / 2 else canvasHeight - (((node.lat - minLat) / (maxLat - minLat)) * canvasHeight)
                    return Offset(x.toFloat(), y.toFloat())
                }

                // Draw all edges
                edges.forEach { edge ->
                    val source = nodes.find { it.id == edge.sourceId }
                    val target = nodes.find { it.id == edge.destId }
                    if (source != null && target != null) {
                        val color = if (edge.status == "Active") Color.Gray else Color.Red.copy(alpha = 0.5f)
                        drawLine(
                            color = color,
                            start = getOffset(source),
                            end = getOffset(target),
                            strokeWidth = 2f
                        )
                    }
                }

                // Highlight MST edges if any
                val mstEdges = uiState.algorithmResults.mst ?: emptyList()
                mstEdges.forEach { edge ->
                    val source = nodes.find { it.id == edge.sourceId }
                    val target = nodes.find { it.id == edge.destId }
                    if (source != null && target != null) {
                        drawLine(
                            color = Color.Green,
                            start = getOffset(source),
                            end = getOffset(target),
                            strokeWidth = 6f
                        )
                    }
                }
                
                // Highlight Shortest Path edges if any
                val shortestPath = uiState.algorithmResults.shortestPath?.path ?: emptyList()
                for (i in 0 until shortestPath.size - 1) {
                    val source = nodes.find { it.id == shortestPath[i] }
                    val target = nodes.find { it.id == shortestPath[i+1] }
                    if (source != null && target != null) {
                        drawLine(
                            color = Color.Blue,
                            start = getOffset(source),
                            end = getOffset(target),
                            strokeWidth = 8f
                        )
                    }
                }

                // Draw nodes
                nodes.forEach { node ->
                    val color = when (node.type) {
                        "Depot" -> Color.Blue
                        "Hospital" -> Color.Red
                        else -> Color.DarkGray
                    }
                    val pos = getOffset(node)
                    drawCircle(
                        color = color,
                        radius = 12f,
                        center = pos
                    )
                }
            }
        }
    }
}
