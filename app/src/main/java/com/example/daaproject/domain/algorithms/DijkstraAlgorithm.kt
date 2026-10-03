package com.example.daaproject.domain.algorithms

import com.example.daaproject.data.entity.Edge
import com.example.daaproject.data.entity.Node
import java.util.PriorityQueue

class DijkstraAlgorithm {
    data class PathResult(val distance: Double, val path: List<String>)

    fun findShortestPath(
        nodes: List<Node>,
        edges: List<Edge>,
        sourceNodeId: String,
        targetNodeId: String
    ): PathResult {
        val adjacencyList = mutableMapOf<String, MutableList<Edge>>()
        for (node in nodes) {
            adjacencyList[node.id] = mutableListOf()
        }
        for (edge in edges) {
            adjacencyList[edge.sourceId]?.add(edge)
        }

        val distances = mutableMapOf<String, Double>()
        val previousNodes = mutableMapOf<String, String?>()
        val pq = PriorityQueue<Pair<String, Double>>(compareBy { it.second })

        for (node in nodes) {
            distances[node.id] = Double.MAX_VALUE
            previousNodes[node.id] = null
        }

        distances[sourceNodeId] = 0.0
        pq.add(Pair(sourceNodeId, 0.0))

        val visited = mutableSetOf<String>()

        while (pq.isNotEmpty()) {
            val (currentNodeId, currentDist) = pq.poll()

            if (!visited.add(currentNodeId)) continue

            if (currentNodeId == targetNodeId) break

            val neighbors = adjacencyList[currentNodeId] ?: emptyList()
            for (edge in neighbors) {
                if (edge.status == "Blocked") continue

                val neighborId = edge.destId
                if (visited.contains(neighborId)) continue

                val newDist = currentDist + edge.distance // Or travelTime based on optimization goal
                if (newDist < (distances[neighborId] ?: Double.MAX_VALUE)) {
                    distances[neighborId] = newDist
                    previousNodes[neighborId] = currentNodeId
                    pq.add(Pair(neighborId, newDist))
                }
            }
        }

        val path = mutableListOf<String>()
        var curr: String? = targetNodeId
        while (curr != null) {
            path.add(curr)
            curr = previousNodes[curr]
            if (curr == sourceNodeId) {
                path.add(curr)
                break
            }
        }
        path.reverse()
        
        val finalDist = distances[targetNodeId] ?: Double.MAX_VALUE
        return PathResult(
            distance = finalDist,
            path = if (finalDist == Double.MAX_VALUE) emptyList() else path
        )
    }
}
