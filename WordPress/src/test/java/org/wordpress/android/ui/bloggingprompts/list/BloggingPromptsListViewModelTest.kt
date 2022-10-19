package org.wordpress.android.ui.bloggingprompts.list

import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ALL

class BloggingPromptsListParentViewModelTest : BaseUnitTest() {

    private val site: SiteModel = mock()

    private val viewModel = BloggingPromptsListViewModel()

    @Test
    fun `Should show Error view if siteModel is null`() {
        viewModel.onOpen(site = null, section = ALL)

        assertEquals(ErrorViewState.Error, viewModel.errorViewState.value)
        assertEquals(ContentViewState.Hidden, viewModel.contentViewState.value)
    }

    @Test
    fun `Should show Error view if section null`() {
        viewModel.onOpen(site = site, section = null)

        assertEquals(ErrorViewState.Error, viewModel.errorViewState.value)
        assertEquals(ContentViewState.Hidden, viewModel.contentViewState.value)
    }
}
