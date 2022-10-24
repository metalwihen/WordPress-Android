package org.wordpress.android.ui.bloggingprompts.list

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.SiteModel

@ExperimentalCoroutinesApi
class BloggingPromptsListParentViewModelTest : BaseUnitTest() {
    private val analyticsTracker: BloggingPromptsListAnalyticsTracker = mock()
    private val provider: BloggingPromptsListSiteProvider = mock()
    private val fetchNewUsecase: BloggingPromptsListFetchNewUsecase = mock()
    private val site: SiteModel = mock()
    private val dispatcher = TestCoroutineDispatcher()

    private val viewModel = BloggingPromptsListParentViewModel(
            provider = provider,
            analyticsTracker = analyticsTracker,
            fetchNewUsecase = fetchNewUsecase,
            defaultDispatcher = dispatcher
    )

    @Test
    fun `Should save siteModel when started`() {
        viewModel.start(site, promptsSections[0])

        verify(provider).setSite(site)
    }

    @Test
    fun `Should fetch new prompts when started`() = runBlockingTest {
        viewModel.start(site, promptsSections[0])

        verify(fetchNewUsecase).fetch(site)
    }

    @Test
    fun `Should track screen accessed when started`() {
        whenever(provider.getSite()).thenReturn(site)

        val promptSection = promptsSections[1]
        viewModel.start(site, promptSection)

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

    @Test
    fun `Should not track screen accessed if the page is already started`() {
        whenever(provider.getSite()).thenReturn(site)
        val promptSection = promptsSections[1]

        viewModel.start(site, promptSection)
        viewModel.start(site, promptSection)
        viewModel.start(site, promptSection)

        verify(analyticsTracker, times(1)).trackScreenShown(site, promptSection)
    }

    @Test
    fun `Should not track tab selected if already on same tab`() {
        whenever(provider.getSite()).thenReturn(site)

        val promptSection = promptsSections[2]
        viewModel.onSectionSelected(promptSection)
        viewModel.onSectionSelected(promptSection)
        viewModel.onSectionSelected(promptSection)

        verify(analyticsTracker, times(1)).trackTabSelected(site, promptSection)
    }
}
