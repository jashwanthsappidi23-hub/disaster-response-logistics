package com.example.daaproject.data

import com.example.daaproject.data.dao.AppDao
import com.example.daaproject.data.entity.*
import kotlinx.coroutines.flow.Flow

class ScenarioRepository(private val dao: AppDao) {
    fun getAllScenarios(): Flow<List<Scenario>> = dao.getAllScenarios()

    fun getNodesForScenarioFlow(scenarioId: String): Flow<List<Node>> = dao.getNodesForScenarioFlow(scenarioId)
    fun getEdgesForScenarioFlow(scenarioId: String): Flow<List<Edge>> = dao.getEdgesForScenarioFlow(scenarioId)
    fun getResourcesForScenarioFlow(scenarioId: String): Flow<List<Resource>> = dao.getResourcesForScenarioFlow(scenarioId)
    fun getDemandsForScenarioFlow(scenarioId: String): Flow<List<Demand>> = dao.getDemandsForScenarioFlow(scenarioId)

    suspend fun getNodesForScenario(scenarioId: String): List<Node> = dao.getNodesForScenario(scenarioId)
    suspend fun getEdgesForScenario(scenarioId: String): List<Edge> = dao.getEdgesForScenario(scenarioId)
    suspend fun getResourcesForScenario(scenarioId: String): List<Resource> = dao.getResourcesForScenario(scenarioId)
    suspend fun getDemandsForScenario(scenarioId: String): List<Demand> = dao.getDemandsForScenario(scenarioId)

    suspend fun updateNode(node: Node) {
        dao.insertNodes(listOf(node))
    }

    suspend fun updateEdge(edge: Edge) {
        dao.insertEdges(listOf(edge))
    }

    suspend fun updateDemand(demand: Demand) {
        dao.insertDemands(listOf(demand))
    }

    suspend fun updateResource(resource: Resource) {
        dao.insertResources(listOf(resource))
    }
}
