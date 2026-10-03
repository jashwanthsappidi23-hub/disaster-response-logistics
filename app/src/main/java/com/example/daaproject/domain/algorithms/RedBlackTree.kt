package com.example.daaproject.domain.algorithms

import com.example.daaproject.data.entity.Demand

enum class Color { RED, BLACK }

class NodeRBT(
    var demand: Demand,
    var color: Color = Color.RED,
    var left: NodeRBT? = null,
    var right: NodeRBT? = null,
    var parent: NodeRBT? = null
)

class RedBlackTree {
    var root: NodeRBT? = null

    // Helper for rotation
    private fun rotateLeft(x: NodeRBT) {
        val y = x.right ?: return
        x.right = y.left
        if (y.left != null) {
            y.left?.parent = x
        }
        y.parent = x.parent
        if (x.parent == null) {
            root = y
        } else if (x == x.parent?.left) {
            x.parent?.left = y
        } else {
            x.parent?.right = y
        }
        y.left = x
        x.parent = y
    }

    private fun rotateRight(x: NodeRBT) {
        val y = x.left ?: return
        x.left = y.right
        if (y.right != null) {
            y.right?.parent = x
        }
        y.parent = x.parent
        if (x.parent == null) {
            root = y
        } else if (x == x.parent?.right) {
            x.parent?.right = y
        } else {
            x.parent?.left = y
        }
        y.right = x
        x.parent = y
    }

    private fun fixInsert(k: NodeRBT) {
        var node = k
        while (node.parent?.color == Color.RED) {
            if (node.parent == node.parent?.parent?.left) {
                val u = node.parent?.parent?.right
                if (u?.color == Color.RED) {
                    node.parent?.color = Color.BLACK
                    u.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    node = node.parent?.parent!!
                } else {
                    if (node == node.parent?.right) {
                        node = node.parent!!
                        rotateLeft(node)
                    }
                    node.parent?.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    node.parent?.parent?.let { rotateRight(it) }
                }
            } else {
                val u = node.parent?.parent?.left
                if (u?.color == Color.RED) {
                    node.parent?.color = Color.BLACK
                    u.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    node = node.parent?.parent!!
                } else {
                    if (node == node.parent?.left) {
                        node = node.parent!!
                        rotateRight(node)
                    }
                    node.parent?.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    node.parent?.parent?.let { rotateLeft(it) }
                }
            }
        }
        root?.color = Color.BLACK
    }

    fun insert(demand: Demand) {
        var node = NodeRBT(demand)
        var y: NodeRBT? = null
        var x = root

        while (x != null) {
            y = x
            // Custom comparison based on priority, fallback to id for uniqueness
            x = if (compareDemands(node.demand, x.demand) < 0) {
                x.left
            } else {
                x.right
            }
        }

        node.parent = y
        if (y == null) {
            root = node
        } else if (compareDemands(node.demand, y.demand) < 0) {
            y.left = node
        } else {
            y.right = node
        }

        if (node.parent == null) {
            node.color = Color.BLACK
            return
        }
        if (node.parent?.parent == null) {
            return
        }
        fixInsert(node)
    }

    private fun compareDemands(d1: Demand, d2: Demand): Int {
        // High priority should be "larger" or "smaller" in tree. Let's say higher priority means higher value
        if (d1.priority != d2.priority) {
            return d1.priority.compareTo(d2.priority)
        }
        // if same priority, earlier deadline is "larger" (more important)
        if (d1.deadline != d2.deadline) {
            return d2.deadline.compareTo(d1.deadline)
        }
        return d1.id.compareTo(d2.id)
    }

    // In-order traversal to get demands sorted by priority
    fun getSortedDemands(): List<Demand> {
        val result = mutableListOf<Demand>()
        fun inOrder(node: NodeRBT?) {
            if (node != null) {
                inOrder(node.right) // Traversing right first for descending order based on our compare func
                result.add(node.demand)
                inOrder(node.left)
            }
        }
        inOrder(root)
        return result
    }
}
