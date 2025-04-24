package com.baidiuk.prewave

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.swing.tree.TreeNode

@RestController
@RequestMapping("/tree")
class EdgeController {
//    @GetMapping("/{nodeId}")
//    fun getTree(@PathVariable nodeId: Int): ResponseEntity<TreeNode> {
//        val tree = service.buildTree(nodeId)
//        return ResponseEntity.ok(tree)
//    }
}