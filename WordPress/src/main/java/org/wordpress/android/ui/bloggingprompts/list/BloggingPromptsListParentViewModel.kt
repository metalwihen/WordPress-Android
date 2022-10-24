package org.wordpress.android.ui.bloggingprompts.list

import androidx.annotation.StringRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.modules.UI_THREAD
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ALL
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ANSWERED
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.NOT_ANSWERED
import org.wordpress.android.viewmodel.ScopedViewModel
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BloggingPromptsListParentViewModel @Inject constructor(
    @Named(UI_THREAD) private val defaultDispatcher: CoroutineDispatcher,
    private val provider: BloggingPromptsListSiteProvider,
    private val fetchNewUsecase: BloggingPromptsListFetchNewUsecase,
    private val analyticsTracker: BloggingPromptsListAnalyticsTracker,
) : ScopedViewModel(defaultDispatcher) {
    private var hasStarted = AtomicBoolean(false)
    private var lastSelectedTab = AtomicReference<PromptSection?>(null)

    fun start(site: SiteModel, currentTab: PromptSection) {
        provider.setSite(site)

        // Prevent redundant tracking during Config Changes
        if (hasStarted.get()) return
        else hasStarted.set(true)
        getSite()?.let { analyticsTracker.trackScreenShown(it, currentTab) }

        fetchNewPrompts(site)
    }

    fun onSectionSelected(currentTab: PromptSection) {
        // Prevent redundant tracking during Config Changes
        if (currentTab == lastSelectedTab.get()) return
        else lastSelectedTab.set(currentTab)
        getSite()?.let { analyticsTracker.trackTabSelected(it, currentTab) }
    }

    fun getSite(): SiteModel? = provider.getSite()

    private fun fetchNewPrompts(site: SiteModel) {
        launch {
            fetchNewUsecase.fetch(site)
        }
    }
}

val promptsSections = listOf(ALL, ANSWERED, NOT_ANSWERED)

internal const val POSITION_DEFAULT_TAB = 0

enum class PromptSection(@StringRes val titleRes: Int) {
    ALL(R.string.blogging_prompts_tab_all),
    ANSWERED(R.string.blogging_prompts_tab_answered),
    NOT_ANSWERED(R.string.blogging_prompts_tab_not_answered)
}
