package org.wordpress.android.ui.bloggingprompts

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.ui.bloggingprompts.list.BloggingPromptsListAnalyticsTracker
import org.wordpress.android.ui.bloggingprompts.list.BloggingPromptsListParentViewModel
import org.wordpress.android.ui.bloggingprompts.list.BloggingPromptsListSiteProvider
import org.wordpress.android.ui.bloggingprompts.list.PromptSection
import org.wordpress.android.ui.bloggingprompts.list.promptsSections

class BloggingPromptsListParentViewModelTest : BaseUnitTest() {
    private val analyticsTracker: BloggingPromptsListAnalyticsTracker = mock()
    private val provider: BloggingPromptsListSiteProvider = mock()
    private val site: SiteModel = mock()

    private val viewModel = BloggingPromptsListParentViewModel(
            provider = provider,
            analyticsTracker = analyticsTracker
    )

    @Test
    fun `Should save siteModel when started`() {
        viewModel.start(site)

        verify(provider).setSite(site)
    }

    @Test
    fun `Should track screen accessed when page opened`() {
        whenever(provider.getSite()).thenReturn(site)

        val promptSection = promptsSections[0]
        viewModel.onOpen(promptSection)

        verify(analyticsTracker).trackScreenShown(site, promptSection)
    }

    @Test
    fun `Should track tab selected when changing tabs`() {
        whenever(provider.getSite()).thenReturn(site)

        val promptSection = promptsSections[2]
        viewModel.onSectionSelected(promptSection)

        verify(analyticsTracker).trackTabSelected(site, promptSection)
    }

    @Test
    fun `Should render tabs in the correct order`() {
        assertEquals(3, promptsSections.size)
        assertEquals(PromptSection.ALL, promptsSections[0])
        assertEquals(PromptSection.ANSWERED, promptsSections[1])
        assertEquals(PromptSection.NOT_ANSWERED, promptsSections[2])
    }
}
