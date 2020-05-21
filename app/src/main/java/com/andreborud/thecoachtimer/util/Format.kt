package com.andreborud.thecoachtimer.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * format time to something readable
 */

object Format {

    @SuppressLint("SimpleDateFormat")
    fun time(time: Long): String {
        return time(time, "H:mm:ss.SS")
    }

    fun timeAndShorten (time: Long): String {
        return when {
            time < 60 * 1000 -> {
                time(time, "ss.SS")
            }
            time < 60 * 60 * 1000 -> {
                time(time, "mm:ss.SS")
            }
            else -> {
                time(time, "H:mm:ss.SS")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun time(time: Long, format: String): String { // TODO test
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val formatter = SimpleDateFormat(format)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        calendar.timeInMillis = time
        return formatter.format(calendar.time)
    }
}