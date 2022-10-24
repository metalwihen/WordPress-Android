package org.wordpress.android.ui.bloggingprompts.list

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.store.bloggingprompts.BloggingPromptsStore
import java.time.LocalDate
import java.util.Date

internal class BloggingPromptsListFetchNewUsecaseTest : BaseUnitTest() {
    private val site: SiteModel = mock()
    private val promptsStore: BloggingPromptsStore = mock()
    private val dateHelper: BloggingPromptsListDateFormatter = mock()

    private val usecase = BloggingPromptsListFetchNewUsecase(promptsStore, dateHelper)

    @Test
    fun `should fetch prompts from 2 weeks ago`() = runBlockingTest {
        val today = LocalDate.of(2022, 1, 15) // Feb 15th
        val twoWeeksBefore = Date(2022, 1, 1) // Feb 1st
        whenever(dateHelper.now()).thenReturn(today)
        whenever(dateHelper.convert(any())).thenReturn(twoWeeksBefore)

        usecase.fetch(site)

        verify(promptsStore).fetchPrompts(site, PROMPT_FETCH_LIMIT, twoWeeksBefore)
    }
}
