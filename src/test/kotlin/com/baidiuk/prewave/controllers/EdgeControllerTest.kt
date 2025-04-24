package com.baidiuk.prewave.controllers

import com.baidiuk.prewave.Tables.EDGE
import com.baidiuk.prewave.dto.TreeNode
import org.jooq.DSLContext
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EdgeControllerTest {

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var dsl: DSLContext

    @LocalServerPort
    var port: Int = 0

    private fun url(path: String) = "http://localhost:$port$path"

    @BeforeAll
    fun cleanBeforeStart() {
        dsl.deleteFrom(EDGE).execute()
    }

    @Test
    @Order(1)
    fun createEdge_success() {
        val body = EdgeRequest(1, 2)
        val response = rest.postForEntity(url("/edges"), body, String::class.java)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    @Order(2)
    fun createEdge_conflict() {
        val body = EdgeRequest(1, 2)
        val response = rest.postForEntity(url("/edges"), body, String::class.java)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Edge from 1 to 2 already exists.", response.body)
    }

    @Test
    @Order(3)
    fun getTree_returnsTreeNode() {
        // already have edge 1→2
        rest.postForEntity(url("/edges"), EdgeRequest(2,3), String::class.java)
        val response = rest.getForEntity(url("/edges/1"), TreeNode::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.id)
        assertEquals(1, response.body?.children?.size)
    }

    @Test
    @Order(4)
    fun deleteEdge_success() {
        val response = rest.exchange(
            url("/edges"),
            HttpMethod.DELETE,
            HttpEntity(EdgeRequest(1, 2)),
            String::class.java
        )
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
//
//    @Test
//    @Order(5)
//    fun deleteEdge_notFound() {
//        val response = rest.exchange(
//            url("/edges"),
//            HttpMethod.DELETE,
//            bodyEntity("""[99,100]"""),
//            String::class.java
//        )
//        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
//    }
//
//    private fun bodyEntity(json: String): HttpEntity<String> {
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        return HttpEntity(json, headers)
//    }
}