package com.baidiuk.prewave

import com.baidiuk.prewave.Tables.EDGE
import com.baidiuk.prewave.dto.EdgeRequest
import com.baidiuk.prewave.dto.TreeNode
import com.baidiuk.prewave.repositories.EdgeRepository
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.util.AssertionErrors.fail
import kotlin.system.measureTimeMillis

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PerformanceTest {
    @Autowired
    lateinit var repo: EdgeRepository

    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var rest: TestRestTemplate

    @LocalServerPort
    var port: Int = 0

    private fun url(path: String) = "http://localhost:$port$path"

    @BeforeEach
    fun cleanBefore() {
        dsl.deleteFrom(EDGE).execute()
    }

    @Test
    fun repoCreate2kEdges() {

        val total = 2000
        var inserted = 0

        val time = measureTimeMillis {
            for (i in 1..total) {
                if (repo.createEdge(i, i + 1) == 1) {
                    inserted++
                }
            }

            val all = repo.getAll()
            assertEquals(total, all.size, "Expected $total edges in DB")
        }

        println("✅ Inserted $inserted edges in ${time}ms")

        if (time > 3_000) {
            fail("💥 Performance FAIL: Took $time ms, expected < 3 s")
        } else {
            println("🚀 Performance OK: ${time}ms")
        }
    }

    @Test
    fun controllerGet2kEdges() {
        val total = 2000

        val time = measureTimeMillis {
            for (i in 1..total) {
                val body = HttpEntity(EdgeRequest(i, i + 1))
                rest.postForEntity(url("/edges"), body, String::class.java)
            }

            // Validate repo has all
            val all = repo.getAll()
            assertEquals(total, all.size)

            // Validate tree call works and returns something
            val response: ResponseEntity<TreeNode> = rest.getForEntity(url("/edges/1"), TreeNode::class.java)
            assertEquals(1, response.body?.id)
        }

        println("✅ HTTP test for $total edges done in ${time}ms")

        if (time > 10_000) {
            fail("💥 Performance FAIL: Took $time ms, expected < 10 s")
        }
    }

}