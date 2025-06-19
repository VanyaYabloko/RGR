package com.example.mycalendarapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

class CalendarPagerAdapter(
    private val context: Context,
    private val events: Map<LocalDate, String>,
    private val onDayClickListener: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarPagerAdapter.MonthViewHolder>() {

    private val currentDate = LocalDate.now()
    private val months = (-6..5).map { currentDate.plusMonths(it.toLong()) }

    inner class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gridView: GridView = view.findViewById(R.id.calendarGrid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.month_layout, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val monthDate = months[position]
        val daysInMonth = getDaysForMonth(monthDate)
        holder.gridView.adapter = DayAdapter(context, daysInMonth, events, onDayClickListener)
    }

    override fun getItemCount(): Int = months.size

    fun getMonthAtPosition(position: Int): LocalDate {
        return months[position]
    }

    private fun getDaysForMonth(date: LocalDate): List<LocalDate> {
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDay = date.withDayOfMonth(1)
        val dayOfWeek = firstDay.dayOfWeek.value % 7

        val days = mutableListOf<LocalDate>()

        // Add empty days for the start of the month
        repeat(dayOfWeek) { days.add(LocalDate.MIN) }

        // Add actual days of the month
        (1..daysInMonth).forEach { day ->
            days.add(date.withDayOfMonth(day))
        }

        return days
    }
}