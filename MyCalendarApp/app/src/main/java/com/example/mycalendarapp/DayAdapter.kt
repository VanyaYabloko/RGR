package com.example.mycalendarapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.threeten.bp.LocalDate

class DayAdapter(
    private val context: Context,
    private val days: List<LocalDate>,
    private val events: Map<LocalDate, String>,
    private val onDayClick: (LocalDate) -> Unit
) : BaseAdapter() {


    override fun getCount(): Int = days.size

    override fun getItem(position: Int): LocalDate = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.day_cell, parent, false)

        val dayText = view.findViewById<TextView>(R.id.dayText)
        val eventText = view.findViewById<TextView>(R.id.eventText)
        val eventIndicator = view.findViewById<View>(R.id.eventIndicator)

        val date = getItem(position)

        if (date == LocalDate.MIN) {
            dayText.text = ""
            eventText.text = ""
            eventIndicator.visibility = View.GONE
        } else {
            dayText.text = date.dayOfMonth.toString()

            val event = events[date]
            if (!event.isNullOrEmpty()) {
                eventText.text = event
                eventIndicator.visibility = View.VISIBLE
            } else {
                eventText.text = ""
                eventIndicator.visibility = View.GONE
            }

            view.setOnClickListener { onDayClick(date) }
        }

        return view
    }
}