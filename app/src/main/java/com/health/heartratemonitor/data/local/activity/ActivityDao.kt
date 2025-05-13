package com.health.heartratemonitor.data.local.activity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(summary: ActivitySummaryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGraph(graph: ActivityGraphEntity)

    @Query("SELECT * FROM activity_summary ORDER BY startTimestamp DESC")
    fun getAllActivities(): Flow<List<ActivitySummaryEntity>>

    @Query("DELETE FROM activity_summary")
    suspend fun deleteAllActivities()

    @Query("DELETE FROM activity_summary WHERE id = :id")
    suspend fun deleteActivityById(id: Long)

    @Query("SELECT * FROM activity_graph WHERE sessionId = :sessionId")
    suspend fun getGraphForSession(sessionId: Long): ActivityGraphEntity?

    @Transaction
    @Query("""SELECT s.*, g.heartRateGraphJson
        FROM activity_summary AS s
        JOIN activity_graph   AS g ON g.sessionId = s.id
        WHERE s.id = :id""")
    suspend fun getActivityDetails(id: Long): ActivitySessionEntityWithGraph
}
