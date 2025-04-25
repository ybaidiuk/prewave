package com.baidiuk.prewave.controllers

import com.baidiuk.prewave.dto.EdgeRequest
import com.baidiuk.prewave.dto.TreeNode
import com.baidiuk.prewave.repositories.EdgeRepository
import com.baidiuk.prewave.services.TreeService
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("/edges")
class EdgeController(
    private val repo: EdgeRepository,
    private val treeService: TreeService
) {

    @PostMapping
    fun createEdge(@RequestBody request: EdgeRequest): ResponseEntity<Any> {
        return try {
//            val inserted =
            repo.createEdge(request.fromId, request.toId)
            ResponseEntity.status(HttpStatus.CREATED).build()
        } catch (_: DuplicateKeyException) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Edge from ${request.fromId} to ${request.toId} already exists.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating edge: ${e.message}")
        }
    }

    //todo test edge case where removing edge can lead to creation of new independence tree.
    @DeleteMapping
    fun deleteEdge(@RequestBody request: EdgeRequest): ResponseEntity<Any> {
        val deleted = repo.deleteEdge(request.fromId, request.toId)
        return if (deleted == 0) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Edge from ${request.fromId} to ${request.toId} not found.")
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