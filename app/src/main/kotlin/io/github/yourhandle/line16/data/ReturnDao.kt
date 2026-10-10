package io.github.yourhandle.line16.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReturnDao {
    @Query("SELECT * FROM returns WHERE year = :year LIMIT 1")
    fun observe(year: Int): Flow<ReturnEntity?>

    @Query("SELECT * FROM returns WHERE year = :year LIMIT 1")
    suspend fun get(year: Int): ReturnEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ReturnEntity)

    @Query("DELETE FROM returns WHERE year = :year")
    suspend fun delete(year: Int)
}
