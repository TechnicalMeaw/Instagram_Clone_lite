package com.example.instagramclonelite.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {
    companion object{
        /**
         * Return date in specified format.
         * @param milliSeconds Date in milliseconds
         * @return String representing date in specified format
         */
        fun getDate(milliSeconds: Long): String? {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.UK)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }
    }
}