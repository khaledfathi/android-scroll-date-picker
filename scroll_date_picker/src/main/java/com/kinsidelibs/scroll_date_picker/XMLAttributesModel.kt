package com.kinsidelibs.scroll_date_picker

import androidx.annotation.ColorInt


/**
 * model to receive some XML attribute to use inside [Adapter]
 *
 * @property selectedColor
 * @property unselectedColor
 * @property todayDateColor
 * @property dayNameColor
 * @property monthNameColor
 * @property dayNumberColor
 * @constructor Create empty xml Attributes model
 */
internal data class XMLAttributesModel(
     @ColorInt var selectedColor: Int,
     @ColorInt var unselectedColor: Int,
     @ColorInt var todayDateColor: Int,
     @ColorInt var dayNameColor: Int,
     @ColorInt var monthNameColor: Int,
     @ColorInt var dayNumberColor: Int,
)
