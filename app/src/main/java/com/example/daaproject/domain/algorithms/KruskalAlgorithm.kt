package com.example.daaproject.domain.algorithms

import com.example.daaproject.data.entity.Edge
import com.example.daaproject.data.entity.Node

class KruskalAlgorithm {
    class DisjointSet(nodes: List<Node>) {
        private val parent = mutableMapOf<String, String>()
        private val rank = mutableMapOf<String, Int>()

        init {
            for (node in nodes) {
                parent[node.id] = node.id
                rank[node.id] = 0
            }
        }

        fun find(item: String): String {
            if (parent[item] == item) {
                return item
            }
            val res = find(parent[item]!!)
            parent[item] = res
            return res
        }

        fun union(set1: String, set2: String): Boolean {
            val root1 = find(set1)
            val root2 = find(set2)
            if (root1 == root2) return false

            val rank1 = rank[root1] ?: 0
            val rank2 = rank[root2] ?: 0

            if (rank1 > rank2) {
                parent[root2] = root1
            } else if (rank1 < rank2) {
                parent[root1] = root2
            } else {
                parent[root2] = root1
                rank[root1] = rank1 + 1
            }
            return true
        }
    }

    fun findMinimumSpanningTree(nodes: List<Node>, edges: List<Edge>): List<Edge> {
        val mst = mutableListOf<Edge>()
        
        // Filter out blocked edges
        val validEdges = edges.filter { it.status != "Blocked" }
        // Sort by distance (or another weight)
        val sortedEdges = validEdges.sortedBy { it.distance }
        
        val disjointSet = DisjointSet(nodes)

        for (edge in sortedEdges) {
            if (disjointSet.union(edge.sourceId, edge.destId)) {
                mst.add(edge)
            }
        }
        return mst
    }
}
