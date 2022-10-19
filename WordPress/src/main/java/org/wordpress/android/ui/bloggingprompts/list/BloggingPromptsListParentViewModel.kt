package org.wordpress.android.ui.bloggingprompts.list

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ALL
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ANSWERED
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.NOT_ANSWERED
import javax.inject.Inject

@HiltViewModel
class BloggingPromptsListParentViewModel @Inject constructor(
    private val provider: BloggingPromptsListSiteProvider,
    private val analyticsTracker: BloggingPromptsListAnalyticsTracker,
) : ViewModel() {
    fun start(site: SiteModel) {
        provider.setSite(site)
    }

    fun onOpen(currentTab: PromptSection) {
        getSite()?.let {
            analyticsTracker.trackScreenShown(it, currentTab)
        }
    }

    fun onSectionSelected(currentTab: PromptSection) {
        getSite()?.let {
            analyticsTracker.trackTabSelected(it, currentTab)
        }
    }

    fun getSite(): SiteModel? = provider.getSite()
}

val promptsSections = listOf(ALL, ANSWERED, NOT_ANSWERED)

enum class PromptSection(@StringRes val titleRes: Int) {
    ALL(R.string.blogging_prompts_tab_all),
    ANSWERED(R.string.blogging_prompts_tab_answered),
    NOT_ANSWERED(R.string.blogging_prompts_tab_not_answered)
}

