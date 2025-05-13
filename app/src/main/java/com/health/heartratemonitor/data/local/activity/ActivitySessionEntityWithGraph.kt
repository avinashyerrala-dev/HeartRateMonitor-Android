// ActivitySessionEntityWithGraph.kt
package com.health.heartratemonitor.data.local.activity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Result of JOIN‑ing activity_summary  ↔  activity_graph
 *
 * Room will auto‑populate it when you use @Transaction or write a JOIN query.
 */
data class ActivitySessionEntityWithGraph(

    @Embedded
    val summary: ActivitySummaryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val graph: ActivityGraphEntity
)
