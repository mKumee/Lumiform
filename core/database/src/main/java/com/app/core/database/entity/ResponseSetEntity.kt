package com.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "response_sets")
data class ResponseSetEntity(
    @PrimaryKey val id: Int,
    val questionItemId: Int,
    val multipleSelection: Boolean
)
