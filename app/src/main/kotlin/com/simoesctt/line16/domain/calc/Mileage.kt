package com.simoesctt.line16.domain.calc

object Mileage {
    /** IRS 2026 standard business mileage rate. Update in TaxYear2026 when published. */
    const val RATE_CENTS_PER_MILE = 70

    fun deduction(miles: Int): Long = Math.round(miles * RATE_CENTS_PER_MILE / 100.0)
}
