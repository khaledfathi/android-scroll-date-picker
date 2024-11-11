package com.kinsidelibs.scroll_date_picker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kinsidelibs.scroll_date_picker.databinding.DateCardBinding
import com.kinsidelibs.scroll_date_picker.listener.OnDateChangeListener
import com.kinsidelibs.scroll_date_picker.listener.OnSelectTodayDateListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Adapter
 *
 * @property context
 * @property data date range list
 * @property fullDateTextView Text view outside of [this adapter][Adapter],
 * used to show current selected date
 * @property xmlAttributeModel custom xml attributes as [xmlAttributeModel]
 * @constructor Create empty Adapter
 */
internal class Adapter(
    private val context: Context,
    private val data: List<LocalDate>,
    private val fullDateTextView: TextView,
    private val xmlAttributeModel: XMLAttributesModel,
) :
    RecyclerView.Adapter<Adapter.VH>() {
    companion object {
        var isFirstLoad = true

        //
        private var _selectedPosition = -1
        private var _lastItemSelected: CardView? = null
        private var _lastDateSelected = LocalDate.now()

        //
        private var _todayPosition = -1
        private var _todayItemSelected: CardView? = null
        private var _isTodaySelected = true
    }

    /**
     * reset companion object static value to its default
     * except [isFirstLoad] variable
     */
    fun resetStatic() {
        _selectedPosition = -1
        _lastItemSelected = null
        _lastDateSelected = LocalDate.now()
        _todayPosition = -1
        _todayItemSelected = null
        _isTodaySelected = true
    }

    //listeners
    var onSelectTodayDateListener: OnSelectTodayDateListener? = null
    var onDateChangeListener: OnDateChangeListener? = null

    //dates formater
    private val _dayNameFormat = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
    private val _monthNameFormat = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())


    //UI colors (selection)
    private var _selectedItemColor = (xmlAttributeModel.selectedColor)
    private var _unselectedItemColor = (xmlAttributeModel.unselectedColor)
    private var _todayItemColor = (xmlAttributeModel.todayDateColor)

    //UI colors (static colors)
    private var _dayNameColor = (xmlAttributeModel.dayNameColor)
    private var _monthNameColor = (xmlAttributeModel.monthNameColor)
    private var _dayNumberColor = (xmlAttributeModel.dayNumberColor)

    class VH(val view: DateCardBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(DateCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        //set UI colors attributes (from custom xml attributes)
        holder.view.dayNameTextView.setTextColor(_dayNameColor)
        holder.view.monthNameTextView.setTextColor(_monthNameColor)
        holder.view.dayNumberTextView.setTextColor(_dayNumberColor)

        //set data
        holder.view.monthNameTextView.text = data[position].format(_monthNameFormat)
        holder.view.dayNameTextView.text = data[position].format(_dayNameFormat)
        holder.view.dayNumberTextView.text =
            String.format(Locale.getDefault(), data[position].dayOfMonth.toString())
        //catch today position
        if (isTodayDate(data[position])) {
            holder.setIsRecyclable(false)
            _todayPosition = holder.adapterPosition
            _todayItemSelected = holder.view.dateCircleCardView
        }
        //default holder color
        holder.view.dateCircleCardView.setCardBackgroundColor(_unselectedItemColor)
        //event
        holder.view.dateCircleCardView.setOnClickListener {
            //save last date obj selected
            _lastDateSelected = data[position]
            //change last selected color
            _lastItemSelected?.setCardBackgroundColor(_unselectedItemColor)
            //trace selected item
            _lastItemSelected = holder.view.dateCircleCardView
            _selectedPosition = holder.adapterPosition
            //set item selected - color
            holder.view.dateCircleCardView.setCardBackgroundColor(_selectedItemColor)

            if (_todayPosition == holder.adapterPosition) {
                _todayItemSelected?.setCardBackgroundColor(_selectedItemColor)
                _isTodaySelected = true
            } else {
                _todayItemSelected?.setCardBackgroundColor(_todayItemColor)
                _isTodaySelected = false
            }

            //set full date to outside text view
            setFullDateTextView(data[position])

            //listener
            onDateChangeListener?.onDateChange(_lastDateSelected)
        }
    }

    override fun onViewAttachedToWindow(holder: VH) {
        //today is NOT selected
        if (_selectedPosition == holder.adapterPosition) {
            holder.view.dateCircleCardView.setCardBackgroundColor(_selectedItemColor)
            _lastItemSelected = holder.view.dateCircleCardView
        } else {
            holder.view.dateCircleCardView.setCardBackgroundColor(_unselectedItemColor)
        }
        //today is selected
        if (_todayPosition == holder.adapterPosition && !_isTodaySelected) {
            holder.view.dateCircleCardView.setCardBackgroundColor(_todayItemColor)
        } else if (_todayPosition == holder.adapterPosition && _isTodaySelected) {
            holder.view.dateCircleCardView.setCardBackgroundColor(_selectedItemColor)
        }
        super.onViewAttachedToWindow(holder)
    }

    /**
     * Set current selected date value to "full date text view" , this element is outside of this Adapter View Holder[VH]
     *
     * @param date current selected date
     */
    private fun setFullDateTextView(date: LocalDate) {
        fullDateTextView.text =
            "${date.format(_dayNameFormat)} ${date.dayOfMonth}/${date.monthValue}/${date.year}"
    }

    /**
     * Check if the [date] is equal to today date
     *
     * @param date date to compare with
     * @return true for match
     */
    private fun isTodayDate(date: LocalDate): Boolean {
        val dayNameFormat = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault())
        val currentDate = date.format(dayNameFormat)
        return LocalDate.now().format(dayNameFormat) == currentDate
    }

    /**
     * calculate : data size - ( data size - today position )
     * @return target position
     */
    fun getTodayPosition(): Int {
        if (_todayPosition == -1) {
            for (i in data.indices) {
                if (isTodayDate(data[i])) {
                    _todayPosition = i
                    break
                }
            }
        }
        return data.size - (data.size - _todayPosition)
    }

    /**
     *  - Select today [view holder][VH] by changing its background color\n
     *  - set today date value to [fullDateTextView] element\n
     *  - listen to [onSelectToday][OnSelectTodayDateListener] and [onDateChange][OnSelectTodayDateListener]
     */
    fun selectTodayItem() {
        _isTodaySelected = true
        onSelectTodayDateListener?.onSelectToday(_selectedPosition, getTodayPosition())
        _selectedPosition = _todayPosition
        _lastDateSelected = LocalDate.now()
        setFullDateTextView(_lastDateSelected)
        onDateChangeListener?.onDateChange(_lastDateSelected)
    }

    /**
     * Set last date value text to [fullDateTextView] element
     */
    fun setLastDate() {
        setFullDateTextView(_lastDateSelected)
    }

    /**
     * Get last date selected
     *
     * @return date object as [LocalDate]
     */
    fun getSelectedDate(): LocalDate {
        return _lastDateSelected
    }
}