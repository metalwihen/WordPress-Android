package org.wordpress.android.ui.bloggingprompts.list

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.model.bloggingprompts.BloggingPromptModel
import org.wordpress.android.fluxc.store.bloggingprompts.BloggingPromptsStore
import org.wordpress.android.fluxc.store.bloggingprompts.BloggingPromptsStore.BloggingPromptsResult
import org.wordpress.android.ui.bloggingprompts.list.TestDummyProvider.getPromptModel

class BloggingPromptsListFetchStoredUsecaseTest : BaseUnitTest() {
    private val promptsStore: BloggingPromptsStore = mock()
    private val site: SiteModel = mock()

    private val usecase = BloggingPromptsListFetchStoredUsecase(promptsStore)

    @Test
    fun `Should return null if store is unable to fetch prompts`() = runBlockingTest {
        val promptSection = PromptSection.ALL
        whenever(promptsStore.getPrompts(site)).thenReturn(flow { emit(BloggingPromptsResult()) })

        val result = usecase.fetch(site, promptSection).first()

        assertEquals(null, result)
    }

    @Test
    fun `Should return empty list if store fetch results in no prompts`() = runBlockingTest {
        val promptSection = PromptSection.ALL
        whenever(promptsStore.getPrompts(site)).thenReturn(flow { emit(BloggingPromptsResult(model = emptyList())) })

        val result = usecase.fetch(site, promptSection).first()

        assertEquals(emptyList<BloggingPromptModel>(), result)
    }

    @Test
    fun `Should return complete list of prompts if fetch is successful`() = runBlockingTest {
        val promptSection = PromptSection.ALL
        val listModels = listOf(
                getPromptModel().copy(id = 14),
                getPromptModel().copy(id = 15)
        )
        whenever(promptsStore.getPrompts(site)).thenReturn(flow { emit(BloggingPromptsResult(model = listModels)) })

        val result = usecase.fetch(site, promptSection).first()

        assertEquals(listModels, result)
    }

    @Test
    fun `Should return filtered list of answered prompts if fetch is successful`() = runBlockingTest {
        val promptSection = PromptSection.ANSWERED
        val listModels = listOf(
                getPromptModel().copy(id = 12, isAnswered = true),
                getPromptModel().copy(id = 13, isAnswered = true),
                getPromptModel().copy(id = 14, isAnswered = false)
        )
        val expectedFilteredList = listOf(
                getPromptModel().copy(id = 12, isAnswered = true),
                getPromptModel().copy(id = 13, isAnswered = true)
        )
        whenever(promptsStore.getPrompts(site)).thenReturn(flow { emit(BloggingPromptsResult(model = listModels)) })

        val result = usecase.fetch(site, promptSection).first()

        assertEquals(expectedFilteredList, result)
    }

    @Test
    fun `Should return filtered list of unanswered prompts if fetch is successful`() = runBlockingTest {
        val promptSection = PromptSection.NOT_ANSWERED
        val listModels = listOf(
                getPromptModel().copy(id = 12, isAnswered = true),
                getPromptModel().copy(id = 13, isAnswered = true),
                getPromptModel().copy(id = 14, isAnswered = false)
        )
        val expectedFilteredList = listOf(
                getPromptModel().copy(id = 14, isAnswered = false)
        )
        whenever(promptsStore.getPrompts(site)).thenReturn(flow { emit(BloggingPromptsResult(model = listModels)) })

        val result = usecase.fetch(site, promptSection).first()

        assertEquals(expectedFilteredList, result)
    }
}
