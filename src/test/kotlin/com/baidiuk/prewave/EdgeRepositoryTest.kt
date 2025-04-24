package com.baidiuk.prewave

import com.baidiuk.prewave.Tables.EDGE
import org.jooq.DSLContext
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // needed for @BeforeAll to work properly and simple.
@SpringBootTest
class EdgeRepositoryTest {

    @Autowired
    lateinit var repo: EdgeRepository

    @Autowired
    lateinit var dsl: DSLContext

    @BeforeAll
    fun cleanBeforeStart() {
        dsl.deleteFrom(EDGE).execute()
    }

    @AfterAll
    fun cleanAfterAll() {
        dsl.deleteFrom(EDGE).execute()
    }

    @Test
    @Order(1)
    fun createTest() {
        val res = repo.createEdge(1, 2)
        assertEquals(1, res)
    }

    @Test
    @Order(2)
    fun getAllTest() {
        val all = repo.getAll()
        assertEquals(1, all.size)
        assertEquals(1, all[0].fromId)
        assertEquals(2, all[0].toId)
    }

    @Test
    @Order(3)
    fun deleteTest() {
        val deleted = repo.deleteEdge(1, 2)
        assertEquals(1, deleted)
    }
    @Test
    @Order(4)
    fun getByNodeIdTEst() {
        repo.createEdge(1, 2)
        repo.createEdge(2, 3)
        repo.createEdge(2, 4)

        repo.createEdge(99, 100)
        val all = repo.fetchTreeFrom(1)
        println(all)
        assertEquals(3, all.size)
    }
}