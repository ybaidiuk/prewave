package com.baidiuk.prewave

import com.baidiuk.prewave.Tables.EDGE
import com.baidiuk.prewave.tables.records.EdgeRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
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

//    data class EdgeRelation(val from: Int, val to: Int)
    /**
     * It was fuc...ng hard.
     */

    fun fetchTreeFrom(rootId: Int): List<Pair<Int, Int>> {
        //create virtual table tree
        val tree = table(name("tree"))
        val nodeId = field("node_id", Int::class.java)
        val fromId = field("from_id", Int::class.java)
        val toId = field("to_id", Int::class.java)

        val recursiveQuery = dsl.withRecursive("tree").`as`(
            select( // not recursive Base query
                EDGE.FROM_ID,
                EDGE.TO_ID,
                EDGE.TO_ID.`as`("node_id")
            )
                .from(EDGE)
                .where(EDGE.FROM_ID.eq(rootId))
                .unionAll(
                    select( // recursive query
                        EDGE.FROM_ID,
                        EDGE.TO_ID,
                        EDGE.TO_ID.`as`("node_id")
                    ).from(EDGE)
                        .join(tree).on(EDGE.FROM_ID.eq(nodeId))
                )
        ).select(fromId, toId).from(tree)
        return recursiveQuery.fetch { Pair<Int, Int>(it[fromId], it[toId]) }
    }
}


