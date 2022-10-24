package org.wordpress.android.ui.bloggingprompts.list

import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.store.bloggingprompts.BloggingPromptsStore
import javax.inject.Inject

class BloggingPromptsListFetchNewUsecase @Inject constructor(
    private val promptsStore: BloggingPromptsStore,
    private val dateHelper: BloggingPromptsListDateFormatter
) {
    suspend fun fetch(site: SiteModel) {
        val fromLocalDate = dateHelper.now()!!.minusDays(TWO_WEEKS_IN_DAYS)
        val fromDate = dateHelper.convert(fromLocalDate)
        promptsStore.fetchPrompts(site, PROMPT_FETCH_LIMIT, fromDate!!)
    }
}

internal const val TWO_WEEKS_IN_DAYS = 14L
internal const val PROMPT_FETCH_LIMIT = 20
