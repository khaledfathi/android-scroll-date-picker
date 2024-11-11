package com.kinsidelibs.scroll_date_picker

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kinsidelibs.scroll_date_picker.databinding.MainLayoutBinding
import com.kinsidelibs.scroll_date_picker.general.Const
import com.kinsidelibs.scroll_date_picker.general.Date
import com.kinsidelibs.scroll_date_picker.general.Dates
import com.kinsidelibs.scroll_date_picker.listener.OnDateChangeListener
import com.kinsidelibs.scroll_date_picker.listener.OnSelectTodayDateListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


/**
 * Scroll date picker
 *
 * @constructor
 * - -read xml attribute
 * - -get Date Range List
 * - -create and set recycle view adapter
 * - -make recycle view scroll to today position and select it
 * - -add the layout to this FrameLayout
 * - -set events for this FrameLayout
 * @param context
 * @param attr
 */
class ScrollDatePicker(context: Context, attr: AttributeSet?) : FrameLayout(context, attr) {

    private var _mainLayoutBinding: MainLayoutBinding =
        MainLayoutBinding.inflate(LayoutInflater.from(context))

    //for recycle view
    private var _dateRecycleView: RecyclerView = _mainLayoutBinding.dateRecycleView
    private var _adapter: Adapter

    //date range
    private lateinit var _startDate: LocalDate
    private lateinit var _endDate: LocalDate
    private var _datesList: List<LocalDate>

    //xml Attribute Model
    private lateinit var _xmlAttributeModel: XMLAttributesModel

    init {
        //xml attributes
        val arr = context.obtainStyledAttributes(attr, R.styleable.ScrollDatePicker, 0, 0)
        setAttributes(arr)
        arr.recycle()
        _datesList = getDateRangeList()
        _adapter = Adapter(
            context,
            _datesList,
            _mainLayoutBinding.fullDateTextView,
            _xmlAttributeModel
        )
        //set adapter
        _dateRecycleView.adapter = _adapter
        //scroll to today position for first time app launched
        if (Adapter.isFirstLoad) {
            _dateRecycleView.scrollToPosition(_adapter.getTodayPosition())
            _adapter.selectTodayItem()
            Adapter.isFirstLoad = false
        } else {
            _adapter.setLastDate()
        }
        //add ScrollDatePicker Object to "root/main" layout
        addView(_mainLayoutBinding.root)
        //set events
        eventTodayButtonClick()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        _datesList = recreateDateRange()
        _adapter.notifyItemRangeChanged(0, _datesList.size)
        super.onConfigurationChanged(newConfig)
    }

    /**
     * Get default date range list
     * @param startYear
     * @param startMonth
     * @param startDay
     * @param endYear
     * @param endMonth
     * @param endDay
     * @return List of [LocalDate]
     *///prepare/create dates range list
    private fun getDateRangeList(
        startYear: Int = Const.START_YEAR,
        startMonth: Int = Const.START_MONTH,
        startDay: Int = Const.START_DAY,
        endYear: Int = Const.END_YEAR,
        endMonth: Int = Const.END_MONTH,
        endDay: Int = Const.END_DAY,
    ): List<LocalDate> {
        _startDate = LocalDate.of(startYear, startMonth, startDay)
        _endDate = LocalDate.of(endYear, endMonth, endDay)
        return Dates.getDateRange(_startDate, _endDate)
    }

    /**
     * used to recreate date range list, used on [onConfigurationChanged] ,
     * for : if user change system language , this function should fire
     *
     * @return List of [LocalDate]
     */
    private fun recreateDateRange(): List<LocalDate> {
        return Dates.getDateRange(_startDate, _endDate)
    }

    /***** Events *****/
    private fun eventTodayButtonClick() {
        _mainLayoutBinding.todayButton.setOnClickListener {
            _mainLayoutBinding.dateRecycleView.scrollToPosition(_adapter.getTodayPosition())
            //set listener
            _adapter.onSelectTodayDateListener = object : OnSelectTodayDateListener {
                override fun onSelectToday(selectedPosition: Int, todayPosition: Int) {
                    val todayHolder =
                        _dateRecycleView.findViewHolderForAdapterPosition(todayPosition)
                    val lastSelectedHolder =
                        _dateRecycleView.findViewHolderForAdapterPosition(selectedPosition)

                    if (selectedPosition != todayPosition) {
                        todayHolder?.setIsRecyclable(true)
                        todayHolder?.itemView?.findViewById<CardView>(R.id.dateCircleCardView)
                            ?.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.selected_date
                                )
                            )
                        todayHolder?.setIsRecyclable(false)

                        lastSelectedHolder?.itemView?.findViewById<CardView>(R.id.dateCircleCardView)
                            ?.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.unselected_date
                                )
                            )
                    }
                }

            }
            _adapter.selectTodayItem()
        }
    }
    /***** -END- Events *****/


    fun getCurrentSelectedDate(): Date {
        return generateDateObject(_adapter.getSelectedDate())
    }

    fun setDateRange(
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int,
        endMonth: Int,
        endDay: Int,
    ) {
        _datesList = getDateRangeList(
            startYear,
            startMonth,
            startDay,
            endYear,
            endMonth,
            endDay
        )
        _adapter = Adapter(
            context,
            _datesList,
            _mainLayoutBinding.fullDateTextView,
            _xmlAttributeModel
        )
        _adapter.resetStatic()
        _dateRecycleView.adapter = _adapter

        _dateRecycleView.scrollToPosition(_adapter.getTodayPosition())
        _adapter.selectTodayItem()
    }

    /**
     * Set on date change
     *
     * @param onDateChange callback to listen to date change
     * @receiver [Adapter] for "date scroll" recycle view
     */
    fun setOnDateChange(onDateChange: (date: Date) -> Unit) {
        _adapter.onDateChangeListener = object : OnDateChangeListener {
            override fun onDateChange(date: LocalDate) {
                onDateChange(generateDateObject(date))
            }
        }
    }

    fun setFullBoxBackgroundColor(@ColorInt color: Int) {
        _mainLayoutBinding.root.setBackgroundColor(color)
    }

    fun setScrollBoxBackgroundColor(@ColorInt color: Int) {
        _mainLayoutBinding.dateRecycleView.setBackgroundColor(color)
    }

    fun setTodayButtonTextColor(@ColorInt color: Int) {
        _mainLayoutBinding.todayButtonText.setTextColor(color)
    }

    fun setTodayButtonBackgroundColor(@ColorInt color: Int) {
        _mainLayoutBinding.todayButton.setCardBackgroundColor(color)
    }

    fun setFullDateTextColor(@ColorInt color: Int) {
        _mainLayoutBinding.fullDateTextView.setTextColor(color)
    }

    fun setFullDateUnderLineColor(@ColorInt color: Int) {
        _mainLayoutBinding.fullDateUnderLine.setBackgroundColor(color)
    }

    fun setButtonBackgroundColor(@ColorInt color: Int) {
        _mainLayoutBinding.todayButton.setBackgroundColor(color)
    }

    fun setButtonTextColor(@ColorInt color: Int) {
        _mainLayoutBinding.todayButtonText.setTextColor(color)
    }

    fun setSelectedDateColor(@ColorInt color: Int) {
        _xmlAttributeModel.selectedColor = color
    }

    fun setUnselectedDateColor(@ColorInt color: Int) {
        _xmlAttributeModel.unselectedColor = color
    }

    fun setTodayUnselectedColor(@ColorInt color: Int) {
        _xmlAttributeModel.todayDateColor = color
    }

    fun setDayNumberColor(@ColorInt color: Int) {
        _xmlAttributeModel.dayNumberColor = color
    }

    fun setDayNameColor(@ColorInt color: Int) {
        _xmlAttributeModel.dayNameColor = color
    }

    fun setMonthNameColor(@ColorInt color: Int) {
        _xmlAttributeModel.monthNameColor = color
    }
    /***** -END- Controls *****/

    /***** UI Attributes ******/
    private fun setAttributes(arr: TypedArray) {
        //--- for recycle view
        _xmlAttributeModel = XMLAttributesModel(
            selectedColor = arr.getColor(
                R.styleable.ScrollDatePicker_selected_color,
                ContextCompat.getColor(context, R.color.selected_date)
            ),
            unselectedColor = arr.getColor(
                R.styleable.ScrollDatePicker_unselected_color,
                ContextCompat.getColor(context, R.color.unselected_date)
            ),
            todayDateColor = arr.getColor(
                R.styleable.ScrollDatePicker_today_date_color,
                ContextCompat.getColor(context, R.color.today_date)
            ),
            dayNameColor = arr.getColor(
                R.styleable.ScrollDatePicker_day_name_color,
                ContextCompat.getColor(context, R.color.day_name)
            ),
            monthNameColor = arr.getColor(
                R.styleable.ScrollDatePicker_month_name_color,
                ContextCompat.getColor(context, R.color.month_name)
            ),
            dayNumberColor = arr.getColor(
                R.styleable.ScrollDatePicker_day_number_color,
                ContextCompat.getColor(context, R.color.day_number)
            ),
        )
        //---for mainlayout
        _mainLayoutBinding.dateRecycleView.setBackgroundColor(
            arr.getColor(
                R.styleable.ScrollDatePicker_scroll_background,
                ContextCompat.getColor(context, R.color.scroll_box)
            )
        )
        _mainLayoutBinding.todayButton.setBackgroundColor(
            arr.getColor(
                R.styleable.ScrollDatePicker_today_button_color,
                ContextCompat.getColor(context, R.color.today_button)
            )
        )
        _mainLayoutBinding.todayButton.findViewById<TextView>(R.id.today_button_text).setTextColor(
            arr.getColor(
                R.styleable.ScrollDatePicker_today_button_text_color,
                ContextCompat.getColor(context, R.color.today_button_text)
            )
        )
        _mainLayoutBinding.fullDateTextView.setTextColor(
            arr.getColor(
                R.styleable.ScrollDatePicker_full_date_color,
                ContextCompat.getColor(context, R.color.full_date_text)
            )
        )
        _mainLayoutBinding.fullDateUnderLine.setBackgroundColor(
            arr.getColor(
                R.styleable.ScrollDatePicker_full_date_under_line_color,
                ContextCompat.getColor(context, R.color.full_date_under_line)
            )
        )
    }
    /***** -END- UI Attributes ******/

    /***** -END- Utilities ******/
    /**
     * Generate [Date] Object from [LocalDate] object
     * @param date
     * @return Date Model
     */
    private fun generateDateObject(date: LocalDate): Date {
        val dayNameFormat = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        val monthNameFormat = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
        return Date(
            date,
            date.dayOfMonth,
            date.monthValue,
            date.year,
            dayNameFormat.format(date),
            monthNameFormat.format(date),
            "${date.year}-${date.monthValue}-${date.dayOfMonth}"
        )
    }
    /***** -END- Utilities ******/
}