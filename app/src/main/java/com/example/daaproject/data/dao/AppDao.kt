package com.example.daaproject.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.daaproject.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenarios(scenarios: List<Scenario>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<Node>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEdges(edges: List<Edge>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<Resource>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDemands(demands: List<Demand>)

    @Query("SELECT * FROM scenarios")
    fun getAllScenarios(): Flow<List<Scenario>>

    @Query("SELECT * FROM nodes WHERE scenarioId = :scenarioId")
    fun getNodesForScenarioFlow(scenarioId: String): Flow<List<Node>>

    @Query("SELECT * FROM edges WHERE scenarioId = :scenarioId")
    fun getEdgesForScenarioFlow(scenarioId: String): Flow<List<Edge>>

    @Query("SELECT * FROM resources WHERE scenarioId = :scenarioId")
    fun getResourcesForScenarioFlow(scenarioId: String): Flow<List<Resource>>

    @Query("SELECT * FROM demands WHERE scenarioId = :scenarioId")
    fun getDemandsForScenarioFlow(scenarioId: String): Flow<List<Demand>>
    
    @Query("SELECT * FROM nodes WHERE scenarioId = :scenarioId")
    suspend fun getNodesForScenario(scenarioId: String): List<Node>

    @Query("SELECT * FROM edges WHERE scenarioId = :scenarioId")
    suspend fun getEdgesForScenario(scenarioId: String): List<Edge>

    @Query("SELECT * FROM resources WHERE scenarioId = :scenarioId")
    suspend fun getResourcesForScenario(scenarioId: String): List<Resource>

    @Query("SELECT * FROM demands WHERE scenarioId = :scenarioId")
    suspend fun getDemandsForScenario(scenarioId: String): List<Demand>
    
    @Query("SELECT COUNT(*) FROM scenarios")
    suspend fun getScenarioCount(): Int
}
