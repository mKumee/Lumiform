package com.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.app.core.database.entity.ItemEntity
import com.app.core.database.entity.PageEntity
import com.app.core.database.entity.ResponseEntity
import com.app.core.database.entity.ResponseSetEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ContentDao {
    @Query("SELECT * FROM pages ORDER BY orderIndex")
    fun observePages(): Flow<List<PageEntity>>

    @Query("SELECT * FROM content_items ORDER BY orderIndex")
    fun observeItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM response_sets")
    fun observeResponseSets(): Flow<List<ResponseSetEntity>>

    @Query("SELECT * FROM responses ORDER BY orderIndex")
    fun observeResponses(): Flow<List<ResponseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<PageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponseSets(responseSets: List<ResponseSetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponses(responses: List<ResponseEntity>)

    @Query("DELETE FROM pages")
    suspend fun clearPages()

    @Query("DELETE FROM content_items")
    suspend fun clearItems()

    @Query("DELETE FROM response_sets")
    suspend fun clearResponseSets()

    @Query("DELETE FROM responses")
    suspend fun clearResponses()

    @Transaction
    suspend fun replaceAll(
        pages: List<PageEntity>,
        items: List<ItemEntity>,
        responseSets: List<ResponseSetEntity>,
        responses: List<ResponseEntity>
    ) {
        clearResponses()
        clearResponseSets()
        clearItems()
        clearPages()
        insertPages(pages)
        insertItems(items)
        insertResponseSets(responseSets)
        insertResponses(responses)
    }

}
