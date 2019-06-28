package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnit = TimeUnit.SECOND): Date {
    this.time += when (units) {
        TimeUnit.SECOND -> value * SECOND
        TimeUnit.MINUTE -> value * MINUTE
        TimeUnit.HOUR -> value * HOUR
        TimeUnit.DAY -> value * DAY
    }
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = (this.time - date.time)
    when {
        Math.abs(diff) / SECOND < 1 -> return "только что"
        Math.abs(diff) / SECOND < 45 -> return if (diff > 0) "несколько секунд назад" else "через несколько секунд"
        Math.abs(diff) / SECOND < 75 -> return if (diff > 0) "минуту назад" else "через минуту"
        Math.abs(diff) / MINUTE < 45 -> {
            val diffMin = Math.abs(diff) / MINUTE
            when {
                ((diffMin % 10) in 2..4) && (diffMin / 10 != 1L) -> return if (diff < 0) "$diffMin минуты назад" else "через $diffMin минуты"
                (diffMin % 10 == 1L) && (diffMin / 10 != 1L) -> return if (diff < 0) "$diffMin минуту назад" else "через $diffMin минуту"
                else -> return if (diff < 0) "$diffMin минут назад" else "через $diffMin минут"
            }
        }
        Math.abs(diff) / MINUTE < 75 -> return if (diff > 0) "час назад" else "через час"
        Math.abs(diff) / HOUR < 22 -> {
            val diffHour = Math.abs(diff) / HOUR
            when {
                ((diffHour % 10) in 2..4) && (diffHour / 10 != 1L) -> return if (diff < 0) "$diffHour часа назад" else "через $diffHour часа"
                (diffHour % 10 == 1L) && (diffHour / 10 != 1L) -> return if (diff < 0) "$diffHour час назад" else "через $diffHour час"
                else -> return if (diff < 0) "$diffHour часов назад" else "через $diffHour часов"
            }
        }
        Math.abs(diff) / HOUR < 26 -> return if (diff > 0) "день назад" else "через день"
        Math.abs(diff) / DAY < 360 -> {
            val diffDay = Math.abs(diff) / DAY
            when {
                ((diffDay % 10) in 2..4) && (diffDay / 10 != 1L) -> return if (diff < 0) "$diffDay дня назад" else "через $diffDay дня"
                (diffDay % 10 == 1L) && (diffDay / 10 != 1L) -> return if (diff < 0) "$diffDay день назад" else "через $diffDay день"
                else -> return if (diff < 0) "$diffDay дней назад" else "через $diffDay дней"
            }
        }
        else -> return if (diff < 0) "более года назад" else "более чем через год"
    }
}

enum class TimeUnit {
    SECOND, MINUTE, HOUR, DAY
}