package org.wordpress.android.ui.bloggingprompts.list

import org.wordpress.android.fluxc.model.bloggingprompts.BloggingPromptModel
import java.time.LocalDate
import java.util.Date

object TestDummyProvider {
    fun getPromptModel() = BloggingPromptModel(
            id = 12,
            text = "What did you do last week?",
            title = "PN2",
            content = "brief description",
            date = Date(LocalDate.of(2022, 1, 3).toEpochDay()),
            isAnswered = true,
            attribution = "asdf",
            respondentsCount = 12,
            respondentsAvatarUrls = emptyList()
    )

    fun getPromptListItem() = BloggingPromptsListItem(
            title = "What did you do last week?",
            dateLabel = "Jan 3",
            respondentsCount = 12,
            isAnswered = true
    )
}
