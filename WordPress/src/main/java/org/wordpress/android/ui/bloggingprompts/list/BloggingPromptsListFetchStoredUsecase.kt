package org.wordpress.android.ui.bloggingprompts.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.model.bloggingprompts.BloggingPromptModel
import org.wordpress.android.fluxc.store.bloggingprompts.BloggingPromptsStore
import javax.inject.Inject

class BloggingPromptsListFetchStoredUsecase @Inject constructor(
    private val promptsStore: BloggingPromptsStore
) {
    fun fetch(site: SiteModel, promptSection: PromptSection): Flow<List<BloggingPromptModel>?> =
            promptsStore.getPrompts(site)
                    .map { it.model }
                    .map { filterBySection(list = it, promptSection) }

    private fun filterBySection(
        list: List<BloggingPromptModel>?,
        promptSection: PromptSection
    ): List<BloggingPromptModel>? =
            list?.filter { model ->
                when (promptSection) {
                    PromptSection.ANSWERED -> model.isAnswered
                    PromptSection.NOT_ANSWERED -> !model.isAnswered
                    PromptSection.ALL -> true
                }
            }
}
