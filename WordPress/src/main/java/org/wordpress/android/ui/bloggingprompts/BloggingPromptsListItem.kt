package org.wordpress.android.ui.bloggingprompts

import java.util.Date
import java.util.Random

data class BloggingPromptsListItem(
    val title: String,
    val date: Date,
    val respondentsCount: Int,
    val isAnswered: Boolean
)

// DUMMY DATA
@Suppress("MagicNumber")
fun generateDummyData() =
        BloggingPromptsListItem(
                title = listOf(
                        "Cast the move of your life.",
                        "What was your favourite ice cream as a kid?",
                        "When did you learn to tell time?",
                        "If I was able to time travel just once, then what would I do with that power?"
                )[Random().nextInt(4)],
                date = Date(),
                respondentsCount = listOf(200, 0, 1, 13)[Random().nextInt(4)],
                isAnswered = listOf(true, false)[Random().nextInt(2)]
        )
