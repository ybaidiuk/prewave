package com.baidiuk.prewave.repositories

import com.baidiuk.prewave.Tables
import com.baidiuk.prewave.tables.records.EdgeRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service

@Service
class EdgeRepository(private val dsl: DSLContext) {
    fun getAll(): List<EdgeRecord> {
        return dsl.selectFrom(Tables.EDGE).fetch()
    }

    fun createEdge(fromId: Int, toId: Int): Int {
        return dsl.insertInto(Tables.EDGE)
            .columns(Tables.EDGE.FROM_ID, Tables.EDGE.TO_ID)
            .values(fromId, toId)
            .execute()
    }

    fun deleteEdge(fromId: Int, toId: Int): Int {
        return dsl.deleteFrom(Tables.EDGE)
            .where(Tables.EDGE.FROM_ID.eq(fromId).and(Tables.EDGE.TO_ID.eq(toId)))
            .execute()
    }

//    data class EdgeRelation(val from: Int, val to: Int)
    /**
     * It was fuc...ng hard.
     */

    fun fetchTreeFrom(rootId: Int): List<Pair<Int, Int>> {
        //create virtual table tree
        val tree = DSL.table(DSL.name("tree"))
        val nodeId = DSL.field("node_id", Int::class.java)
        val fromId = DSL.field("from_id", Int::class.java)
        val toId = DSL.field("to_id", Int::class.java)

        val recursiveQuery = dsl.withRecursive("tree").`as`(
            DSL.select( // not recursive Base query
                Tables.EDGE.FROM_ID,
                Tables.EDGE.TO_ID,
                Tables.EDGE.TO_ID.`as`("node_id")
            )
                .from(Tables.EDGE)
                .where(Tables.EDGE.FROM_ID.eq(rootId))
                .unionAll(
                    DSL.select( // recursive query
                        Tables.EDGE.FROM_ID,
                        Tables.EDGE.TO_ID,
                        Tables.EDGE.TO_ID.`as`("node_id")
                    ).from(Tables.EDGE)
                        .join(tree).on(Tables.EDGE.FROM_ID.eq(nodeId))
                )
        ).select(fromId, toId).from(tree)
        return recursiveQuery.fetch { Pair<Int, Int>(it[fromId], it[toId]) }
    }
}