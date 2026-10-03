package com.example.daaproject.data.db

import com.example.daaproject.data.dao.AppDao
import com.example.daaproject.data.entity.*

object SampleDataPopulator {
    suspend fun populateData(dao: AppDao) {
        if (dao.getScenarioCount() > 0) return

        val scenarios = mutableListOf<Scenario>()
        val nodes = mutableListOf<Node>()
        val edges = mutableListOf<Edge>()
        val resources = mutableListOf<Resource>()
        val demands = mutableListOf<Demand>()

        val scenarioNames = listOf(
            "Kathmandu Earthquake",
            "Pokhara Flood",
            "Chitwan Landslide",
            "Lalitpur Wildfire",
            "Bhaktapur Earthquake",
            "Everest Base Camp Avalanche",
            "Annapurna Blizzard",
            "Lumbini Heatwave",
            "Dharan Storm",
            "Janakpur Epidemic"
        )

        for (i in 0 until 10) {
            val sId = "S_$i"
            scenarios.add(
                Scenario(id = sId, name = scenarioNames[i], description = "Sample scenario ${i + 1}")
            )

            // Create some nodes
            for (j in 0 until 5) {
                val nId = "N_${i}_${j}"
                val type = if (j == 0) "Depot" else if (j == 1) "Hospital" else "Shelter"
                nodes.add(
                    Node(
                        id = nId,
                        scenarioId = sId,
                        name = "Node $j for Scenario $i",
                        type = type,
                        lat = 27.0 + j * 0.1 + i * 0.01,
                        lng = 85.0 + j * 0.1 + i * 0.01,
                        capacity = 1000 + j * 100,
                        availableResources = if (type == "Depot") 500 else 50,
                        priority = j + 1
                    )
                )

                // Add resources
                if (type == "Depot") {
                    resources.add(Resource(id = "R_${i}_${j}_food", scenarioId = sId, nodeId = nId, type = "Food", quantity = 100, weight = 10, value = 50))
                    resources.add(Resource(id = "R_${i}_${j}_med", scenarioId = sId, nodeId = nId, type = "Medical", quantity = 50, weight = 5, value = 100))
                }

                // Add demands
                if (type != "Depot") {
                    demands.add(
                        Demand(
                            id = "D_${i}_${j}",
                            scenarioId = sId,
                            nodeId = nId,
                            population = 500,
                            foodReq = 100,
                            waterReq = 150,
                            medicalReq = 50,
                            priority = 5 - j,
                            deadline = System.currentTimeMillis() + 86400000,
                            status = "Pending"
                        )
                    )
                }
            }

            // Create edges forming a small network
            val edgesList = listOf(
                Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4), Pair(0, 3), Pair(1, 4)
            )
            for (k in edgesList.indices) {
                val (u, v) = edgesList[k]
                edges.add(
                    Edge(
                        id = "E_${i}_${k}",
                        scenarioId = sId,
                        sourceId = "N_${i}_${u}",
                        destId = "N_${i}_${v}",
                        distance = 10.0 + k,
                        travelTime = 30.0 + k * 2,
                        capacity = 100,
                        riskLevel = (k % 3) + 1,
                        disruptionProb = 0.1 * (k % 5),
                        status = "Open"
                    )
                )
                // Add reverse edge for undirected/bidirectional paths
                edges.add(
                    Edge(
                        id = "E_${i}_${k}_rev",
                        scenarioId = sId,
                        sourceId = "N_${i}_${v}",
                        destId = "N_${i}_${u}",
                        distance = 10.0 + k,
                        travelTime = 30.0 + k * 2,
                        capacity = 100,
                        riskLevel = (k % 3) + 1,
                        disruptionProb = 0.1 * (k % 5),
                        status = "Open"
                    )
                )
            }
        }

        dao.insertScenarios(scenarios)
        dao.insertNodes(nodes)
        dao.insertEdges(edges)
        dao.insertResources(resources)
        dao.insertDemands(demands)
    }
}
