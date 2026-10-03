package com.example.daaproject.domain.algorithms

import com.example.daaproject.data.entity.Edge
import com.example.daaproject.data.entity.Node
import java.util.PriorityQueue

class PrimAlgorithm {

    fun findMinimumSpanningTree(nodes: List<Node>, edges: List<Edge>): List<Edge> {
        if (nodes.isEmpty()) return emptyList()

        val adjacencyList = mutableMapOf<String, MutableList<Edge>>()
        for (node in nodes) {
            adjacencyList[node.id] = mutableListOf()
        }
        for (edge in edges) {
            if (edge.status != "Blocked") {
                adjacencyList[edge.sourceId]?.add(edge)
            }
        }

        val mst = mutableListOf<Edge>()
        val visited = mutableSetOf<String>()
        val pq = PriorityQueue<Edge>(compareBy { it.distance })

        val startNode = nodes.first().id
        visited.add(startNode)
        
        adjacencyList[startNode]?.forEach { pq.add(it) }

        while (pq.isNotEmpty() && visited.size < nodes.size) {
            val edge = pq.poll() ?: break
            
            val destNode = edge.destId
            if (!visited.contains(destNode)) {
                visited.add(destNode)
                mst.add(edge)
                
                adjacencyList[destNode]?.forEach { nextEdge ->
                    if (!visited.contains(nextEdge.destId)) {
                        pq.add(nextEdge)
                    }
                }
            }
        }
        
        return mst
    }
}
