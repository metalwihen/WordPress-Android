package org.wordpress.android.ui.bloggingprompts.list

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Test
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.ui.bloggingprompts.list.TestDummyProvider.getPromptListItem
import org.wordpress.android.ui.bloggingprompts.list.TestDummyProvider.getPromptModel

class BloggingPromptsListItemMapperTest : BaseUnitTest() {
    private val dateFormatter: BloggingPromptsListDateFormatter = mock()

    private val mapper = BloggingPromptsListItemMapper(dateFormatter)

    @Test
    fun `Should map a PromptModel to PromptListItem`() {
        whenever(dateFormatter.format(any(), any())).thenReturn("Jan 3")
        val promptModel = getPromptModel()
        val expectedListItem = getPromptListItem()

        val actualListItem = mapper.toUiModel(promptModel)

        Assert.assertEquals(expectedListItem, actualListItem)
    }
}
