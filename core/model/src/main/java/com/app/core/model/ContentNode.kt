package com.app.core.model

sealed interface ContentNode {
    val id: Int
}

/** Top-level element. A list of [Page] is the root of the whole document. */
data class Page(
    override val id: Int,
    val title: String,
    val items: List<ContentNode>
) : ContentNode

/** Sections can nest arbitrarily deep and contain more sections or questions. */
data class Section(
    override val id: Int,
    val title: String,
    val items: List<ContentNode>
) : ContentNode

/** Marker for the three question variants, so exhaustive `when` blocks stay tight. */
sealed interface Question : ContentNode

data class TextQuestion(
    override val id: Int,
    val content: String
) : Question

data class ImageQuestion(
    override val id: Int,
    val src: String,
    val title: String
) : Question

data class ChoiceQuestion(
    override val id: Int,
    val content: String,
    val responseSet: ResponseSet
) : Question

data class ResponseSet(
    val id: Int,
    val multipleSelection: Boolean,
    val responses: List<Response>
)

data class Response(
    val id: Int,
    val label: String,
    val score: Int?
)
