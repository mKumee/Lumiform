package com.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "responses")
data class ResponseEntity(
    @PrimaryKey val id: Int,
    val responseSetId: Int,
    val label: String,
    val score: Int?,
    val orderIndex: Int
)
