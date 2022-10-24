package org.wordpress.android.ui.bloggingprompts.list

import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Test
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ALL
import org.wordpress.android.ui.bloggingprompts.list.TestDummyProvider.getPromptListItem
import org.wordpress.android.ui.bloggingprompts.list.TestDummyProvider.getPromptModel
import org.wordpress.android.util.NetworkUtilsWrapper

@ExperimentalCoroutinesApi
class BloggingPromptsListViewModelTest : BaseUnitTest() {
    private val site: SiteModel = mock()
    private val mainDispatcher = TestCoroutineDispatcher()
    private val fetchUsecase: BloggingPromptsListFetchStoredUsecase = mock()
    private val networkUtilsWrapper: NetworkUtilsWrapper = mock()
    private val mapper: BloggingPromptsListItemMapper = mock()
    val observer = mock<Observer<UiState>>()

    private val viewModel = BloggingPromptsListViewModel(
            mainDispatcher,
            fetchUsecase,
            networkUtilsWrapper,
            mapper
    )

    @Test
    fun `When opening page, Should show Error view if siteModel is null`() {
        viewModel.onOpen(siteModel = null, section = ALL)

        assertEquals(ErrorViewState.Error, viewModel.uiState.value?.errorViewState)
        assertEquals(ContentViewState.Hidden, viewModel.uiState.value?.contentViewState)
    }

    @Test
    fun `When opening page, Should show Error view if section null`() {
        viewModel.onOpen(siteModel = site, section = null)

        assertEquals(ErrorViewState.Error, viewModel.uiState.value?.errorViewState)
        assertEquals(ContentViewState.Hidden, viewModel.uiState.value?.contentViewState)
    }

    @Test
    fun `When clicking retry button, Should show Error view if siteModel is null`() {
        viewModel.onClickButtonRetry()

        assertEquals(ErrorViewState.Error, viewModel.uiState.value?.errorViewState)
        assertEquals(ContentViewState.Hidden, viewModel.uiState.value?.contentViewState)
    }

    @Test
    fun `When clicking retry button, Should show Loading view before fetching`() {
        viewModel.onOpen(site, ALL)
        viewModel.uiState.observeForever(observer)

        viewModel.onClickButtonRetry()

        verify(observer).onChanged(UiState(ContentViewState.Hidden, ErrorViewState.Loading))
    }

    @Test
    fun `When opening page, Should show Loading view before fetching`() {
        viewModel.uiState.observeForever(observer)
        viewModel.onOpen(site, ALL)

        verify(observer).onChanged(UiState(ContentViewState.Hidden, ErrorViewState.Loading))
    }

    @Test
    fun `When opening page, Should show NoConnection view if network unavailable`() {
        whenever(networkUtilsWrapper.isNetworkAvailable()).thenReturn(false)

        viewModel.onOpen(site, ALL)

        assertEquals(ErrorViewState.NoConnection, viewModel.uiState.value?.errorViewState)
        assertEquals(ContentViewState.Hidden, viewModel.uiState.value?.contentViewState)
    }

    @Test
    fun `When opening page, Should show Error view if prompt fetch fails`() {
        whenever(networkUtilsWrapper.isNetworkAvailable()).thenReturn(true)
        whenever(fetchUsecase.fetch(eq(site), any())).thenReturn(flow { emit(null) })

        viewModel.onOpen(site, ALL)

        assertEquals(ErrorViewState.Error, viewModel.uiState.value?.errorViewState)
        assertEquals(ContentViewState.Hidden, viewModel.uiState.value?.contentViewState)
    }

    @Test
    fun `When opening page, Should show Empty view if prompt fetch fails`() {
        whenever(networkUtilsWrapper.isNetworkAvailable()).thenReturn(true)
        whenever(fetchUsecase.fetch(eq(site), any())).thenReturn(flow { emit(emptyList()) })

        viewModel.onOpen(site, ALL)

        assertEquals(ErrorViewState.Empty, viewModel.uiState.value?.errorViewState)
        assertEquals(ContentViewState.Hidden, viewModel.uiState.value?.contentViewState)
    }

    @Test
    fun `When opening page, Should show Content view if prompt fetch and mapping succeeds`() {
        val listModels = listOf(getPromptModel())
        val listItems = listOf(getPromptListItem())
        whenever(networkUtilsWrapper.isNetworkAvailable()).thenReturn(true)
        whenever(fetchUsecase.fetch(eq(site), any())).thenReturn(flow { emit(listModels) })
        whenever(mapper.toUiModel(any())).thenReturn(listItems[0])

        viewModel.onOpen(site, ALL)

        assertEquals(ErrorViewState.Hidden, viewModel.uiState.value?.errorViewState)
        assertEquals(ContentViewState.Content(listItems), viewModel.uiState.value?.contentViewState)
    }

    @Test
    fun `When opening page, Should show Error view if fetch succeeds but mapping fails`() {
        val listItems = listOf(
                getPromptModel(),
                getPromptModel()
        )
        whenever(networkUtilsWrapper.isNetworkAvailable()).thenReturn(true)
        whenever(fetchUsecase.fetch(eq(site), any())).thenReturn(flow { emit(listItems) })
        whenever(mapper.toUiModel(listItems[1])).thenThrow(IllegalStateException())

        viewModel.onOpen(site, ALL)

        assertEquals(ErrorViewState.Error, viewModel.uiState.value?.errorViewState)
        assertEquals(ContentViewState.Hidden, viewModel.uiState.value?.contentViewState)
    }
}
