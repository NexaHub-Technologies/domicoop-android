package com.nexahub.domicoop.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Port of lib/utils/format.ts */
object Format {
    fun initials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (parts.isEmpty()) return "?"
        return (parts.first().first().toString() + (parts.getOrNull(1)?.first()?.toString() ?: "")).uppercase()
    }

    fun formatDate(iso: String?): String {
        if (iso.isNullOrBlank()) return "—"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date: Date = try {
                parser.parse(iso.take(19)) ?: return iso
            } catch (_: Exception) {
                val alt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                alt.parse(iso.take(10)) ?: return iso
            }
            SimpleDateFormat("d MMM yyyy", Locale.US).format(date)
        } catch (_: Exception) {
            iso
        }
    }

    /** "2026-03" → "March 2026" */
    fun formatMonth(yearMonth: String): String {
        return try {
            val (y, m) = yearMonth.split("-").map { it.toInt() }
            val months = listOf(
                "", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December",
            )
            "${months.getOrElse(m) { m.toString() }} $y"
        } catch (_: Exception) {
            yearMonth
        }
    }

    fun relativeTime(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(iso.take(19)) ?: return iso
            val diff = System.currentTimeMillis() - date.time
            val minutes = diff / 60_000
            val hours = diff / 3_600_000
            val days = diff / 86_400_000
            when {
                minutes < 1 -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                days < 7 -> "${days}d ago"
                else -> formatDate(iso)
            }
        } catch (_: Exception) {
            iso
        }
    }

    fun formatTime(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(iso.take(19)) ?: return ""
            SimpleDateFormat("hh:mm a", Locale.US).format(date)
        } catch (_: Exception) {
            ""
        }
    }

    fun monthLabel(yearMonth: String?): String {
        if (yearMonth.isNullOrBlank()) return "—"
        return formatMonth(yearMonth)
    }

    /** "loan_repayment" → "Loan Repayment"; falls back when blank. */
    fun humanize(value: String?, fallback: String = "—"): String {
        if (value.isNullOrBlank()) return fallback
        return value
            .replace('_', ' ')
            .replace('-', ' ')
            .split(' ')
            .filter { it.isNotEmpty() }
            .joinToString(" ") {
                it.lowercase().replaceFirstChar { c -> c.uppercaseChar() }
            }
    }

    fun round1(value: Double): String = String.format(Locale.US, "%.1f", value)

    fun currency(amount: Double): String = Currency.formatMoney(amount).removePrefix("₦")

    fun currentMonthLong(): String = SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())
}
