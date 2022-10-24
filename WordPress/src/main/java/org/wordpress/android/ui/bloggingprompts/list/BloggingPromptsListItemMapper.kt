package org.wordpress.android.ui.bloggingprompts.list

import org.wordpress.android.fluxc.model.bloggingprompts.BloggingPromptModel
import javax.inject.Inject

class BloggingPromptsListItemMapper @Inject constructor(
    private val dateFormatter: BloggingPromptsListDateFormatter
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
