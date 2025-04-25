package com.baidiuk.prewave.services

import com.baidiuk.prewave.dto.TreeNode
import com.baidiuk.prewave.repositories.EdgeRepository
import org.springframework.stereotype.Service

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