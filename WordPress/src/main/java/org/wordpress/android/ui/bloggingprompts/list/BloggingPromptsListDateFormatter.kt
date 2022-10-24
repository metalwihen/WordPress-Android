package org.wordpress.android.ui.bloggingprompts.list

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class BloggingPromptsListDateFormatter @Inject constructor() {
    fun now(): LocalDate? = LocalDate.now()
    fun convert(localDate: LocalDate): Date? = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    fun format(date: Date, locale: Locale = Locale.getDefault()): String =
            SimpleDateFormat(PROMPT_ITEM_DATE_FORMAT, locale).format(date)
}

private const val PROMPT_ITEM_DATE_FORMAT = "MMM d"

