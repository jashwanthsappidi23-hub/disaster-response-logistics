package com.example.daaproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.daaproject.data.ScenarioRepository
import com.example.daaproject.data.entity.Demand
import com.example.daaproject.data.entity.Edge
import com.example.daaproject.data.entity.Node
import com.example.daaproject.data.entity.Resource
import com.example.daaproject.domain.algorithms.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class KPIMetrics(
    val totalAffectedPeople: Int = 0,
    val activeDepots: Int = 0,
    val demandSatisfactionPercent: Double = 0.0,
    val totalResourcesAvailable: Int = 0
)

data class AlgorithmResults(
    val shortestPath: DijkstraAlgorithm.PathResult? = null,
    val mst: List<Edge>? = null,
    val primMst: List<Edge>? = null,
    val knapsackResult: KnapsackAlgorithm.KnapsackResult? = null,
    val resourceAllocations: List<GreedyAlgorithm.Allocation>? = null,
    val rbtSortedDemands: List<Demand>? = null
)

data class ScenarioUiState(
    val isLoading: Boolean = true,
    val nodes: List<Node> = emptyList(),
    val edges: List<Edge> = emptyList(),
    val resources: List<Resource> = emptyList(),
    val demands: List<Demand> = emptyList(),
    val metrics: KPIMetrics = KPIMetrics(),
    val algorithmResults: AlgorithmResults = AlgorithmResults(),
    val activeScenarioId: String? = null
)

class ScenarioViewModel(
    private val repository: ScenarioRepository
) : ViewModel() {

    private data class DataPack(
        val nodes: List<Node>,
        val edges: List<Edge>,
        val resources: List<Resource>,
        val demands: List<Demand>
    )

    private data class TriggerPack(
        val sourceId: String?,
        val targetId: String?,
        val capacity: Int
    )

    private val _uiState = MutableStateFlow(ScenarioUiState())
    val uiState: StateFlow<ScenarioUiState> = _uiState.asStateFlow()

    private val dijkstra = DijkstraAlgorithm()
    private val kruskal = KruskalAlgorithm()
    private val prim = PrimAlgorithm()
    private val knapsack = KnapsackAlgorithm()
    private val greedy = GreedyAlgorithm()
    private val rbt = RedBlackTree()

    private var currentScenarioId: String? = null

    // For specific algorithm triggers
    private val _sourceNodeIdForPath = MutableStateFlow<String?>(null)
    private val _targetNodeIdForPath = MutableStateFlow<String?>(null)
    private val _knapsackCapacity = MutableStateFlow(100)

    fun setScenarioId(scenarioId: String) {
        if (currentScenarioId == scenarioId) return
        currentScenarioId = scenarioId
        _uiState.update { it.copy(activeScenarioId = scenarioId, isLoading = true) }

        viewModelScope.launch {
            combine(
                combine(
                    repository.getNodesForScenarioFlow(scenarioId),
                    repository.getEdgesForScenarioFlow(scenarioId),
                    repository.getResourcesForScenarioFlow(scenarioId),
                    repository.getDemandsForScenarioFlow(scenarioId)
                ) { n, e, r, d -> DataPack(n, e, r, d) },
                combine(
                    _sourceNodeIdForPath,
                    _targetNodeIdForPath,
                    _knapsackCapacity
                ) { s, t, c -> TriggerPack(s, t, c) }
            ) { dataPack, triggerPack ->
                val nodes = dataPack.nodes
                val edges = dataPack.edges
                val resources = dataPack.resources
                val demands = dataPack.demands
                val sourceId = triggerPack.sourceId
                val targetId = triggerPack.targetId
                val capacity = triggerPack.capacity

                // Recalculate KPIs
                val metrics = calculateKPIs(nodes, resources, demands, edges)
                
                // Recalculate algorithms
                val results = runAlgorithms(
                    nodes = nodes,
                    edges = edges,
                    resources = resources,
                    demands = demands,
                    sourceId = sourceId,
                    targetId = targetId,
                    capacity = capacity
                )
                
                ScenarioUiState(
                    isLoading = false,
                    nodes = nodes,
                    edges = edges,
                    resources = resources,
                    demands = demands,
                    metrics = metrics,
                    algorithmResults = results,
                    activeScenarioId = scenarioId
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun calculateKPIs(
        nodes: List<Node>,
        resources: List<Resource>,
        demands: List<Demand>,
        edges: List<Edge>
    ): KPIMetrics {
        val totalAffected = demands.sumOf { it.population }
        val activeDepots = nodes.count { it.type == "Depot" }
        
        val totalFoodReq = demands.sumOf { it.foodReq }
        val totalWaterReq = demands.sumOf { it.waterReq }
        val totalMedicalReq = demands.sumOf { it.medicalReq }
        val totalReq = totalFoodReq + totalWaterReq + totalMedicalReq
        
        val allocations = greedy.allocateResources(demands, resources)
        val satisfiedReq = allocations.sumOf { it.amountAllocated }
        
        val satisfactionPercent = if (totalReq > 0) {
            (satisfiedReq.toDouble() / totalReq) * 100
        } else {
            100.0
        }
        
        val totalRes = resources.sumOf { it.quantity }
        
        return KPIMetrics(
            totalAffectedPeople = totalAffected,
            activeDepots = activeDepots,
            demandSatisfactionPercent = satisfactionPercent,
            totalResourcesAvailable = totalRes
        )
    }

    private fun runAlgorithms(
        nodes: List<Node>,
        edges: List<Edge>,
        resources: List<Resource>,
        demands: List<Demand>,
        sourceId: String?,
        targetId: String?,
        capacity: Int
    ): AlgorithmResults {
        
        // 1. Shortest Path (Dijkstra)
        val shortestPath = if (sourceId != null && targetId != null) {
            dijkstra.findShortestPath(nodes, edges, sourceId, targetId)
        } else null

        // 2. MST (Kruskal)
        val mst = if (nodes.isNotEmpty() && edges.isNotEmpty()) {
            kruskal.findMinimumSpanningTree(nodes, edges)
        } else null

        // 3. MST (Prim)
        val primMst = if (nodes.isNotEmpty() && edges.isNotEmpty()) {
            prim.findMinimumSpanningTree(nodes, edges)
        } else null

        // 4. Knapsack
        val knapsackRes = knapsack.solve(capacity, resources)
        
        // 5. Greedy Allocation
        val allocationRes = greedy.allocateResources(demands, resources)
        
        // 6. Red-Black Tree Search
        // Build RBT
        val tree = RedBlackTree()
        demands.forEach { tree.insert(it) }
        val sortedDemands = tree.getSortedDemands()
        
        return AlgorithmResults(
            shortestPath = shortestPath,
            mst = mst,
            primMst = primMst,
            knapsackResult = knapsackRes,
            resourceAllocations = allocationRes,
            rbtSortedDemands = sortedDemands
        )
    }

    fun findPath(sourceId: String, targetId: String) {
        _sourceNodeIdForPath.value = sourceId
        _targetNodeIdForPath.value = targetId
    }

    fun setKnapsackCapacity(capacity: Int) {
        _knapsackCapacity.value = capacity
    }
    
    // Updates
    fun updateNode(node: Node) {
        viewModelScope.launch { repository.updateNode(node) }
    }

    fun updateEdge(edge: Edge) {
        viewModelScope.launch { repository.updateEdge(edge) }
    }

    fun updateDemand(demand: Demand) {
        viewModelScope.launch { repository.updateDemand(demand) }
    }

    fun updateResource(resource: Resource) {
        viewModelScope.launch { repository.updateResource(resource) }
    }
}

class ScenarioViewModelFactory(
    private val repository: ScenarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScenarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScenarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
