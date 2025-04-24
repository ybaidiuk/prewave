package com.baidiuk.prewave

import com.baidiuk.prewave.Tables.EDGE
import com.baidiuk.prewave.tables.records.EdgeRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class EdgeRepository(private val dsl: DSLContext) {
    fun getAll(): List<EdgeRecord> {
        return dsl.selectFrom(EDGE).fetch()
    }

    fun createEdge(fromId: Int, toId: Int): Int {
        return dsl.insertInto(EDGE)
            .columns(EDGE.FROM_ID, EDGE.TO_ID)
            .values(fromId, toId)
            .execute()
    }

    fun deleteEdge(fromId: Int, toId: Int): Int {
        return dsl.deleteFrom(EDGE)
            .where(EDGE.FROM_ID.eq(fromId).and(EDGE.TO_ID.eq(toId)))
            .execute()
    }

//    fun findChildren(parentId: Int): List<Int> {
//        return dsl.select(EDGE.TO_ID)
//            .from(EDGE)
//            .where(EDGE.FROM_ID.eq(parentId))
//            .fetch(EDGE.TO_ID)
//    }


}