package com.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_items")
data class ItemEntity(
    @PrimaryKey val id: Int,
    val parentPageId: Int?,
    val parentItemId: Int?,
    val type: String, // "section", "text", "image", "choice"
    val title: String?,      // section, image
    val content: String?,    // text, choice
    val imageSrc: String?,   // image
    val orderIndex: Int
)

object ItemType {
    const val SECTION = "section"
    const val TEXT = "text"
    const val IMAGE = "image"
    const val CHOICE = "choice"
}
