package org.wordpress.android.ui.bloggingprompts

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

data class BloggingPromptsListItem(
    val title: String,
    val dateLabel: String,
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
                dateLabel = SimpleDateFormat(PROMPT_ITEM_DATE_FORMAT, Locale.getDefault()).format(Date()),
                respondentsCount = listOf(200, 0, 1, 13)[Random().nextInt(4)],
                isAnswered = listOf(true, false)[Random().nextInt(2)]
        )

const val PROMPT_ITEM_DATE_FORMAT = "MMM d"
