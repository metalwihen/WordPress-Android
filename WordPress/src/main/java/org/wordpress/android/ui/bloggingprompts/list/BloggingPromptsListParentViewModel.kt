package org.wordpress.android.ui.bloggingprompts.list

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ALL
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ANSWERED
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.NOT_ANSWERED
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

@HiltViewModel
class BloggingPromptsListParentViewModel @Inject constructor(
    private val provider: BloggingPromptsListSiteProvider,
    private val analyticsTracker: BloggingPromptsListAnalyticsTracker,
) : ViewModel() {
    private var hasStarted = AtomicBoolean(false)
    private var lastSelectedTab = AtomicReference<PromptSection?>(null)

    fun start(site: SiteModel, currentTab: PromptSection) {
        provider.setSite(site)
        // Prevent redundant tracking during Config Changes
        if (hasStarted.get()) return
        else hasStarted.set(true)
        getSite()?.let { analyticsTracker.trackScreenShown(it, currentTab) }
    }

    fun onSectionSelected(currentTab: PromptSection) {
        // Prevent redundant tracking during Config Changes
        if (currentTab == lastSelectedTab.get()) return
        else lastSelectedTab.set(currentTab)
        getSite()?.let { analyticsTracker.trackTabSelected(it, currentTab) }
    }

    fun getSite(): SiteModel? = provider.getSite()
}

val promptsSections = listOf(ALL, ANSWERED, NOT_ANSWERED)

internal const val POSITION_DEFAULT_TAB = 0

enum class PromptSection(@StringRes val titleRes: Int) {
    ALL(R.string.blogging_prompts_tab_all),
    ANSWERED(R.string.blogging_prompts_tab_answered),
    NOT_ANSWERED(R.string.blogging_prompts_tab_not_answered)
}

