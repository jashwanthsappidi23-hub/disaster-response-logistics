package com.example.daaproject.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenarios")
data class Scenario(
    @PrimaryKey val id: String,
    val name: String,
    val description: String
)

@Entity(tableName = "nodes")
data class Node(
    @PrimaryKey val id: String,
    val scenarioId: String,
    val name: String,
    val type: String, // Depot, Hospital, Shelter, etc.
    val lat: Double,
    val lng: Double,
    val capacity: Int,
    val availableResources: Int,
    val priority: Int
)

@Entity(tableName = "edges")
data class Edge(
    @PrimaryKey val id: String,
    val scenarioId: String,
    val sourceId: String,
    val destId: String,
    val distance: Double,
    val travelTime: Double,
    val capacity: Int,
    val riskLevel: Int,
    val disruptionProb: Double,
    val status: String // Open, Damaged, Blocked
)

@Entity(tableName = "resources")
data class Resource(
    @PrimaryKey val id: String,
    val scenarioId: String,
    val nodeId: String, // Where it is located
    val type: String, // Food, Water, Medical, Fuel, Vehicles, Rescue Teams
    val quantity: Int,
    val weight: Int, // e.g. for knapsack algorithm
    val value: Int // e.g. importance/utility for knapsack
)

@Entity(tableName = "demands")
data class Demand(
    @PrimaryKey val id: String,
    val scenarioId: String,
    val nodeId: String, // Where it is needed
    val population: Int,
    val foodReq: Int,
    val waterReq: Int,
    val medicalReq: Int,
    val priority: Int,
    val deadline: Long,
    val status: String
)
