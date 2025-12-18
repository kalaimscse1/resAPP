package com.warriortech.resb.util

import kotlinx.datetime.*

object DateTimeUtil {
    
    fun getCurrentDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    
    fun getCurrentDate(): LocalDate {
        return Clock.System.todayIn(TimeZone.currentSystemDefault())
    }
    
    fun getCurrentTimestamp(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
    
    fun formatDate(date: LocalDate, pattern: String = "yyyy-MM-dd"): String {
        return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
    }
    
    fun formatDateTime(dateTime: LocalDateTime): String {
        return "${dateTime.date} ${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}:${dateTime.second.toString().padStart(2, '0')}"
    }
    
    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun parseDateTime(dateTimeString: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateTimeString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun timestampToLocalDateTime(timestamp: Long): LocalDateTime {
        return Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }
}
