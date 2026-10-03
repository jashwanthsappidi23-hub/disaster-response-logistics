package com.example.daaproject.domain.algorithms

import com.example.daaproject.data.entity.Resource
import kotlin.math.max

class KnapsackAlgorithm {
    data class KnapsackResult(val maxValue: Int, val selectedResources: List<Resource>)

    fun solve(capacity: Int, resources: List<Resource>): KnapsackResult {
        val n = resources.size
        val dp = Array(n + 1) { IntArray(capacity + 1) { 0 } }

        for (i in 1..n) {
            val resource = resources[i - 1]
            val w = resource.weight
            val v = resource.value

            for (c in 0..capacity) {
                if (w <= c) {
                    dp[i][c] = max(dp[i - 1][c], dp[i - 1][c - w] + v)
                } else {
                    dp[i][c] = dp[i - 1][c]
                }
            }
        }

        val selectedResources = mutableListOf<Resource>()
        var res = dp[n][capacity]
        var w = capacity

        for (i in n downTo 1) {
            if (res <= 0) break
            if (res == dp[i - 1][w]) {
                continue
            } else {
                val resource = resources[i - 1]
                selectedResources.add(resource)
                res -= resource.value
                w -= resource.weight
            }
        }

        return KnapsackResult(dp[n][capacity], selectedResources)
    }
}
