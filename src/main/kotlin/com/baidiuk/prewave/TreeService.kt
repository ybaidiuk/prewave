package com.baidiuk.prewave

import org.springframework.stereotype.Service


data class TreeNode(val id: Int, val children: List<TreeNode> = emptyList())
@Service
class TreeService(private val repo: EdgeRepository) {

    fun buildTree(rootId: Int): TreeNode {
        val flatList = repo.fetchTreeFrom(rootId)

        val childrenMap = flatList.groupBy({ it.first }, { it.second })

        fun buildNode(id: Int): TreeNode {
            val children = childrenMap[id]?.map { buildNode(it) } ?: emptyList()
            return TreeNode(id, children)
        }

        return buildNode(rootId)
    }
}