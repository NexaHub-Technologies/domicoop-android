package com.nexahub.domicoop.data.remote

/** UI-friendly loan model — port of lib/types/loans.ts transformLoan. */
data class LoanUi(
    val id: String,
    val type: String,
    val purpose: String,
    val title: String,
    val loanCode: String,
    val totalAmount: Double,
    val remainingBalance: Double,
    val status: String, // on_track|pending|completed|overdue|rejected
    val progress: Int,
    val interestRate: Double,
    val termMonths: Int,
    val monthlyPayment: Double,
    val startDate: String,
    val nextDate: String,
    val nextAmount: Double,
    val daysLeft: Int,
)

fun mapLoanStatus(server: String?): String = when (server?.lowercase()) {
    "repaying", "disbursed", "approved", "on_track" -> "on_track"
    "closed", "completed", "paid" -> "completed"
    "overdue", "late", "defaulted" -> "overdue"
    "rejected", "declined", "cancelled" -> "rejected"
    else -> "pending"
}

fun ApiLoan.toUi(): LoanUi {
    val approved = amountApproved ?: amountRequested
    val remaining = balance ?: approved
    val status = mapLoanStatus(status)
    val progress = if (approved > 0) {
        ((approved - remaining) / approved * 100).toInt().coerceIn(0, 100)
    } else 0
    val code = id.take(8).uppercase()
    val monthly = monthlyRepayment ?: if ((tenureMonths ?: 0) > 1) {
        approved * 1.1 / maxOf(1, (tenureMonths ?: 2) - 1)
    } else approved
    return LoanUi(
        id = id,
        type = type,
        purpose = purpose,
        title = purpose.take(42),
        loanCode = code,
        totalAmount = approved,
        remainingBalance = remaining.coerceAtLeast(0.0),
        status = status,
        progress = if (status == "completed") 100 else progress,
        interestRate = interestRate ?: 10.0,
        termMonths = tenureMonths ?: 0,
        monthlyPayment = monthly,
        startDate = disbursedAt ?: createdAt ?: "",
        nextDate = dueDate ?: "",
        nextAmount = monthly,
        daysLeft = 0,
    )
}
