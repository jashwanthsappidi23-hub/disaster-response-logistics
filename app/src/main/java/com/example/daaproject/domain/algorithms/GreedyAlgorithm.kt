package com.example.daaproject.domain.algorithms

import com.example.daaproject.data.entity.Demand
import com.example.daaproject.data.entity.Resource
import kotlin.math.min

class GreedyAlgorithm {
    data class Allocation(
        val demandId: String,
        val resourceId: String,
        val amountAllocated: Int
    )

    // A greedy approach to allocate resources to demands.
    // We prioritize demands with higher priority (lower priority number, e.g., 1 is highest),
    // and allocate resources to fulfill them.
    fun allocateResources(demands: List<Demand>, resources: List<Resource>): List<Allocation> {
        val sortedDemands = demands.sortedBy { it.priority }
        val allocations = mutableListOf<Allocation>()
        
        // Mutable copy of resource quantities
        val resourceQuantities = resources.associate { it.id to it.quantity }.toMutableMap()
        
        for (demand in sortedDemands) {
            var neededFood = demand.foodReq
            var neededWater = demand.waterReq
            var neededMedical = demand.medicalReq
            
            for (resource in resources) {
                val available = resourceQuantities[resource.id] ?: 0
                if (available <= 0) continue
                
                when (resource.type) {
                    "Food" -> {
                        if (neededFood > 0) {
                            val allocate = min(neededFood, available)
                            allocations.add(Allocation(demand.id, resource.id, allocate))
                            neededFood -= allocate
                            resourceQuantities[resource.id] = available - allocate
                        }
                    }
                    "Water" -> {
                        if (neededWater > 0) {
                            val allocate = min(neededWater, available)
                            allocations.add(Allocation(demand.id, resource.id, allocate))
                            neededWater -= allocate
                            resourceQuantities[resource.id] = available - allocate
                        }
                    }
                    "Medical" -> {
                        if (neededMedical > 0) {
                            val allocate = min(neededMedical, available)
                            allocations.add(Allocation(demand.id, resource.id, allocate))
                            neededMedical -= allocate
                            resourceQuantities[resource.id] = available - allocate
                        }
                    }
                }
            }
        }
        
        return allocations
    }
}
