package com.nexahub.domicoop.core

/** Port of lib/utils/contributionAllocation.ts */
data class ContributionAllocation(
    val shares: Double = 0.0,
    val social: Double = 0.0,
    val savings: Double = 0.0,
    val deposit: Double = 0.0,
)

object Allocation {
    const val SHARES_FIXED = 4000.0
    const val SOCIAL_FIXED = 1000.0
    const val MAX_SAVINGS = 46000.0
    const val STANDARD_MAX = 51000.0
    const val SHARES_BENCHMARK = 6000.0

    fun calculate(amount: Double): ContributionAllocation {
        if (amount < SHARES_BENCHMARK) {
            return ContributionAllocation(shares = amount)
        }
        if (amount <= STANDARD_MAX) {
            return ContributionAllocation(
                shares = SHARES_FIXED,
                social = SOCIAL_FIXED,
                savings = maxOf(0.0, amount - SHARES_FIXED - SOCIAL_FIXED),
            )
        }
        return ContributionAllocation(
            shares = SHARES_FIXED,
            social = SOCIAL_FIXED,
            savings = MAX_SAVINGS,
            deposit = amount - STANDARD_MAX,
        )
    }

    fun totals(amounts: List<Double>): ContributionAllocation {
        var s = 0.0; var so = 0.0; var sa = 0.0; var d = 0.0
        for (a in amounts) {
            val c = calculate(a)
            s += c.shares; so += c.social; sa += c.savings; d += c.deposit
        }
        return ContributionAllocation(s, so, sa, d)
    }
}
