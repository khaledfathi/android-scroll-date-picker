package com.kinsidelibs.scroll_date_picker.listener

import java.time.LocalDate

internal interface OnDateChangeListener {
    fun onDateChange(date:LocalDate);
}