package org.wordpress.android.ui.bloggingprompts.list

import org.wordpress.android.fluxc.model.bloggingprompts.BloggingPromptModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class BloggingPromptsListItemMapper @Inject constructor(
    private val dateFormatter: PromptDateFormatter
) {
    fun toUiModel(domainModel: BloggingPromptModel): BloggingPromptsListItem {
        return BloggingPromptsListItem(
                title = domainModel.text,
                dateLabel = dateFormatter.format(domainModel.date),
                respondentsCount = domainModel.respondentsCount,
                isAnswered = domainModel.isAnswered
        )
    }
}

class PromptDateFormatter @Inject constructor() {
    fun format(date: Date, locale: Locale = Locale.getDefault()): String =
            SimpleDateFormat(PROMPT_ITEM_DATE_FORMAT, locale).format(date)
}

private const val PROMPT_ITEM_DATE_FORMAT = "MMM d"
