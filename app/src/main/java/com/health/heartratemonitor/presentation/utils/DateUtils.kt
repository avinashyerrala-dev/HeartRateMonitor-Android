package com.health.heartratemonitor.presentation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy @ hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDuration(start: Long, end: Long): String {
        val minutes = ((end - start) / 1000 / 60).toInt()
        val seconds = ((end - start) / 1000 % 60).toInt()
        return String.format("%02d:%02d", minutes, seconds)
    }
}