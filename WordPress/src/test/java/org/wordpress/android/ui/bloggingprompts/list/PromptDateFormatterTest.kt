package org.wordpress.android.ui.bloggingprompts.list

import org.junit.Assert.assertEquals
import org.junit.Test
import org.wordpress.android.BaseUnitTest
import java.util.Date
import java.util.Locale

internal class PromptDateFormatterTest : BaseUnitTest() {
    private val promptDateFormatter = BloggingPromptsListDateFormatter()

    @Test
    fun `Should format date provided`() {
        val date = Date(2022, 0, 7)

        val dateLabel = promptDateFormatter.format(date, locale = Locale.UK)

        assertEquals("Jan 7", dateLabel)
    }
}
