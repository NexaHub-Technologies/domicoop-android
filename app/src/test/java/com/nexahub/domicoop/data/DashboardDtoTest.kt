package com.nexahub.domicoop.data

import com.nexahub.domicoop.data.remote.DashboardSummary
import com.nexahub.domicoop.data.remote.DomiJson
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.serialization.decodeFromString
import org.junit.Test

class DashboardDtoTest {
    @Test
    fun paidThisMonth_parsesBooleanFalse_fromCrashReport() {
        // Exact shape from the production crash:
        // ..."paid_this_month":false,"current_month":"2026-10",...
        val raw = """{"member":{"full_name":"Test"},"total_savings":149000.0,"paid_this_month":false,"current_month":"2026-10"}"""
        val parsed: DashboardSummary = DomiJson.decodeFromString(raw)
        assertFalse(parsed.paidThisMonth)
        assertEquals("2026-10", parsed.currentMonth)
        assertEquals(149000.0, parsed.totalSavings)
    }

    @Test
    fun paidThisMonth_parsesBooleanTrue() {
        val raw = """{"paid_this_month":true,"current_month":"2026-10"}"""
        val parsed: DashboardSummary = DomiJson.decodeFromString<DashboardSummary>(raw)
        assertTrue(parsed.paidThisMonth)
    }

    @Test
    fun paidThisMonth_acceptsLegacyNumericCache() {
        // Stale on-device cache may hold the old Double shape.
        val zero: DashboardSummary = DomiJson.decodeFromString("""{"paid_this_month":0.0}""")
        assertFalse(zero.paidThisMonth)
        val one: DashboardSummary = DomiJson.decodeFromString("""{"paid_this_month":1.0}""")
        assertTrue(one.paidThisMonth)
    }

    @Test
    fun paidThisMonth_defaultsFalseWhenMissing() {
        val parsed: DashboardSummary = DomiJson.decodeFromString("""{}""")
        assertFalse(parsed.paidThisMonth)
    }
}
