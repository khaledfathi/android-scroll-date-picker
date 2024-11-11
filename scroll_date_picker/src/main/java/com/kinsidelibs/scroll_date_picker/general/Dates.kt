package com.kinsidelibs.scroll_date_picker.general

import java.time.LocalDate

internal class Dates {
    companion object {
        fun getDateRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
            val dateRange = mutableListOf<LocalDate>()
            var currentDate = startDate
            while (currentDate <= endDate) {
                dateRange.add(currentDate)
                currentDate = currentDate.plusDays(1)
            }
            return dateRange
        }
    }
}

/**
 * Date data class to Simplify [LocalDate] object
 *
 * @property localDateObject
 * @property day
 * @property month
 * @property year
 * @property dayName
 * @property monthName
 * @property isoFormat date in format yyyy/MM/dd
 * @constructor Create empty Date
 *///result Date model
data class Date(
    val localDateObject: LocalDate,
    val day: Int,
    val month: Int,
    val year: Int,
    val dayName: String,
    val monthName: String,
    val isoFormat: String
)

