package com.baidiuk.prewave.controllers

import com.baidiuk.prewave.repositories.EdgeRepository
import com.baidiuk.prewave.TreeNode
import com.baidiuk.prewave.services.TreeService
import com.baidiuk.prewave.dto.TreeNode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/edges")
class EdgeController(
    private val repo: EdgeRepository,
    private val treeService: TreeService
) {

    @PostMapping
    fun createEdge(@RequestBody request: Pair<Int,Int>): ResponseEntity<Any> {
        return try {
            val inserted = repo.createEdge(request.first, request.second)
            if (inserted == 0) {
                ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Edge from ${request.first} to ${request.second} already exists.")
            } else {
                ResponseEntity.status(HttpStatus.CREATED).build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating edge: ${e.message}")
        }
    }

    //todo test edge case where removing edge can lead to creation of new independence tree.
    @DeleteMapping
    fun deleteEdge(@RequestBody request: Pair<Int,Int>): ResponseEntity<Any> {
        val deleted = repo.deleteEdge(request.first, request.second)
        return if (deleted == 0) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Edge from ${request.first} to ${request.second} not found.")
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @GetMapping("/{nodeId}")
    fun getTree(@PathVariable nodeId: Int): ResponseEntity<TreeNode> {
        val tree = treeService.buildTree(nodeId)
        return ResponseEntity.ok(tree)
    }

}