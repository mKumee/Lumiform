package com.app.core.model

sealed interface ContentItems {
    val id: Int
}

data class Page(
    override val id: Int,
    val title: String,
    val items: List<ContentItems>
) : ContentItems

data class Section(
    override val id: Int,
    val title: String,
    val items: List<ContentItems>
) : ContentItems

sealed interface Question : ContentItems

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
