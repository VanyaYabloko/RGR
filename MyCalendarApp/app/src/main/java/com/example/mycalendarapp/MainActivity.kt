package com.example.mycalendarapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var monthYearText: TextView
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var adapter: CalendarPagerAdapter

    private val events = mutableMapOf<LocalDate, String>().apply {
        // Добавляем тестовые события для демонстрации
        val today = LocalDate.now()
        put(today, "Тестовое событие 1")
        put(today.plusDays(1), "Тестовое событие 2")
        put(today.minusDays(1), "Тестовое событие 3")
        put(today.withDayOfMonth(15), "Событие в середине месяца")
        put(today.plusMonths(1).withDayOfMonth(5), "Событие в следующем месяце")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidThreeTen.init(this)

        viewPager = findViewById(R.id.calendarViewPager)
        monthYearText = findViewById(R.id.monthYearText)
        addEventButton = findViewById(R.id.addEventButton)

        // ПЕРЕДАЕМ СОБЫТИЯ В АДАПТЕР
        adapter = CalendarPagerAdapter(this, events) { date ->
            showEventDialog(date)
        }

        viewPager.adapter = adapter
        viewPager.setCurrentItem(6, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateMonthYearText(position)
            }
        })

        addEventButton.setOnClickListener {
            val currentMonth = adapter.getMonthAtPosition(viewPager.currentItem)
            showEventDialog(currentMonth.withDayOfMonth(1))
        }

        updateMonthYearText(viewPager.currentItem)
    }

    private fun updateMonthYearText(position: Int) {
        val date = adapter.getMonthAtPosition(position)
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        monthYearText.text = date.format(formatter)
    }

    private fun showEventDialog(date: LocalDate) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_event, null)
        val eventEditText = dialogView.findViewById<EditText>(R.id.eventEditText)

        // Предзаполняем существующее событие
        events[date]?.let { eventEditText.setText(it) }

        AlertDialog.Builder(this)
            .setTitle("Событие на ${date.dayOfMonth}.${date.monthValue}.${date.year}")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val event = eventEditText.text.toString()
                if (event.isNotEmpty()) {
                    events[date] = event

                    // ОБНОВЛЯЕМ ВСЕ АДАПТЕРЫ
                    viewPager.adapter?.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Отмена", null)
            .setNeutralButton("Удалить") { _, _ ->
                events.remove(date)

                // ОБНОВЛЯЕМ ВСЕ АДАПТЕРЫ
                viewPager.adapter?.notifyDataSetChanged()
            }
            .show()
    }
}