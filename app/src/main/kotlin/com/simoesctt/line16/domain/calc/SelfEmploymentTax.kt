package com.simoesctt.line16.domain.calc

import com.simoesctt.line16.domain.year.TaxYear2025

object SelfEmploymentTax {
    data class Result(val ssTax: Long, val medicareTax: Long, val total: Long, val deductibleHalf: Long)

    fun compute(netProfit: Long, otherWages: Long = 0L): Result {
        val netEarnings = Math.round(netProfit * TaxYear2025.SE_NET_EARNINGS_FACTOR)
        if (netEarnings <= 0) return Result(0, 0, 0, 0)

        val ssBase = (TaxYear2025.SS_WAGE_BASE - otherWages).coerceAtLeast(0L)
        val ssTaxable = minOf(netEarnings, ssBase)
        val ss = Math.round(ssTaxable * TaxYear2025.SS_RATE)
        val medicare = Math.round(netEarnings * TaxYear2025.MEDICARE_RATE)
        val total = ss + medicare
        return Result(ss, medicare, total, total / 2)
    }
}
