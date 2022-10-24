package org.wordpress.android.ui.bloggingprompts.list

data class BloggingPromptsListItem(
    val title: String,
    val dateLabel: String,
    val respondentsCount: Int,
    val isAnswered: Boolean
)
