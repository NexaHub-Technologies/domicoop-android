package com.nexahub.domicoop.core

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class AllocationTest {
    @Test
    fun tier0_goesFullyToShares() {
        val a = Allocation.calculate(5000.0)
        assertEquals(5000.0, a.shares)
        assertEquals(0.0, a.social)
        assertEquals(0.0, a.savings)
    }

    @Test
    fun standard_splits4000_1000_rest() {
        val a = Allocation.calculate(51000.0)
        assertEquals(4000.0, a.shares)
        assertEquals(1000.0, a.social)
        assertEquals(46000.0, a.savings)
        assertEquals(0.0, a.deposit)
    }

    @Test
    fun overflow_goesToDeposit() {
        val a = Allocation.calculate(61000.0)
        assertEquals(4000.0, a.shares)
        assertEquals(46000.0, a.savings)
        assertEquals(10000.0, a.deposit)
    }
}

class CurrencyTest {
    @Test
    fun parseNairaInput_stripsSymbols() {
        assertEquals(50000.0, Currency.parseNairaInput("₦50,000"))
        assertEquals(50000.5, Currency.parseNairaInput("50,000.50"))
    }

    @Test
    fun nairaToKobo_converts() {
        assertEquals(500000L, Currency.nairaToKobo(5000.0))
    }
}

class LoanCalcTest {
    @Test
    fun flatInterest_totalIs110pct() {
        val (total, monthly, count) = LoanCalc.calculate(100000.0, 6)
        assertEquals(110000.0, total, 0.01)
        assertEquals(5, count)
        assertEquals(total / count, monthly, 0.01)
    }

    @Test
    fun schedule_lastAbsorbsRounding() {
        val rows = LoanCalc.schedulePreview(100000.0, 6)
        assertEquals(5, rows.size)
        val sum = rows.sumOf { it.amount }
        assertTrue(kotlin.math.abs(sum - 110000.0) < 0.02)
    }
}

class FormatTest {
    @Test
    fun humanize_snakeCaseToTitle() {
        assertEquals("Loan Repayment", Format.humanize("loan_repayment"))
        assertEquals("Contribution", Format.humanize("contribution"))
        assertEquals("Contribution", Format.humanize(null, "Contribution"))
        assertEquals("Contribution", Format.humanize("", "Contribution"))
    }

    @Test
    fun formatDate_isoWithOffsetToShortDate() {
        assertEquals("1 Sep 2027", Format.formatDate("2027-09-01T00:00:00+01:00"))
        assertEquals("30 Aug 2027", Format.formatDate("2027-08-30T00:00:00"))
        assertEquals("—", Format.formatDate(null))
    }
}
