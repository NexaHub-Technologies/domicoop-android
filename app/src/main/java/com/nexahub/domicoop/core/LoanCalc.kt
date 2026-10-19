package com.nexahub.domicoop.core

/** Port of constants/loans.ts loan maths (flat interest, not amortised). */
object LoanCalc {
    const val MIN_AMOUNT = 50_000.0
    const val MAX_AMOUNT = 10_000_000.0
    const val DEFAULT_RATE_PCT = 10.0
    const val MIN_TERM = 1
    const val MAX_TERM = 12

    data class ScheduleRow(
        val installmentNo: Int,
        val dueLabel: String,
        val amount: Double,
        val dueOn: String = dueLabel,
    )

    fun totalRepayment(amount: Double, ratePct: Double = DEFAULT_RATE_PCT): Double =
        amount * (1 + ratePct / 100.0)

    fun monthlyPayment(amount: Double, termMonths: Int, ratePct: Double = DEFAULT_RATE_PCT): Double {
        val count = maxOf(1, termMonths - 1)
        return totalRepayment(amount, ratePct) / count
    }

    /** Flat interest: total = amount * (1 + rate). Split over (term-1) installments. */
    fun calculate(amount: Double, termMonths: Int, ratePct: Double = DEFAULT_RATE_PCT): Triple<Double, Double, Int> {
        val total = amount * (1 + ratePct / 100.0)
        val count = maxOf(1, termMonths - 1)
        val monthly = total / count
        return Triple(total, monthly, count)
    }

    fun schedulePreview(amount: Double, termMonths: Int, ratePct: Double = DEFAULT_RATE_PCT): List<ScheduleRow> {
        val (total, _, count) = calculate(amount, termMonths, ratePct)
        val base = kotlin.math.floor(total / count * 100) / 100.0
        val rows = mutableListOf<ScheduleRow>()
        var acc = 0.0
        for (i in 1..count) {
            val value = if (i == count) (kotlin.math.round((total - acc) * 100) / 100.0) else base
            acc += value
            rows.add(ScheduleRow(i, "Month ${i + 1}", value))
        }
        return rows
    }
}

object ContributionRules {
    const val MIN_AMOUNT = 5000.0
    const val MAX_AMOUNT = 10_000_000.0

    /** Next 12 YYYY-MM options. */
    fun contributionMonths(): List<String> {
        val cal = java.util.Calendar.getInstance()
        return (0 until 12).map {
            val c = (cal.clone() as java.util.Calendar).apply { add(java.util.Calendar.MONTH, it) }
            "%04d-%02d".format(c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH) + 1)
        }
    }
}

object PaystackFees {
    /** 1.5% loan repayment fee shown in make-payment summary. */
    fun feeFor(amount: Double): Double = kotlin.math.round(amount * 0.015 * 100) / 100.0
}
