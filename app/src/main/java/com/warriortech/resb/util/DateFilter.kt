package com.warriortech.resb.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateModern(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Customize your date format
    return currentDate.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTimeModern(): String {
    val currentTime = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("hh:mm a") // Customize your time format (e.g., "hh:mm a" for 12-hour format)
    return currentTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateTimeWithAmPm(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a", Locale("en", "IN"))
    return currentDateTime.format(formatter)
}