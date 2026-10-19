package com.nexahub.domicoop.core

import java.text.NumberFormat
import java.util.Locale

/** Port of lib/utils/currency.ts. Server = Naira number, Paystack = kobo. */
object Currency {
    fun nairaToKobo(naira: Double): Long = Math.round(naira * 100)

    fun koboToNaira(kobo: Long): Double = kobo / 100.0

    /** "₦50,000" / "50,000.50" → 50000.0 ; 2dp max like toApiAmount. */
    fun parseNairaInput(raw: String): Double? {
        val cleaned = raw.replace("₦", "").replace(",", "").replace(" ", "").trim()
        if (cleaned.isEmpty()) return null
        return cleaned.toDoubleOrNull()
    }

    fun requireApiAmount(naira: Double, min: Double = 1.0): Double {
        require(naira in min..99_999_999.99) { "Amount out of range" }
        val scaled = Math.round(naira * 100) / 100.0
        return scaled
    }

    private val ngnNoKobo: NumberFormat =
        NumberFormat.getNumberInstance(Locale("en", "NG")).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }

    private val ngnSigned: NumberFormat =
        NumberFormat.getNumberInstance(Locale("en", "NG")).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }

    fun formatNaira(amount: Double): String = "₦${ngnNoKobo.format(amount)}"

    fun formatMoney(amount: Double): String = "₦${ngnNoKobo.format(amount)}"

    fun formatSigned(amount: Double): String {
        val sign = if (amount < 0) "-" else "+"
        return "$sign₦${ngnSigned.format(kotlin.math.abs(amount))}"
    }
}
