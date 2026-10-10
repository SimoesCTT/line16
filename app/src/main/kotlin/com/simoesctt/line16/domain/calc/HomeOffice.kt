package com.simoesctt.line16.domain.calc

object HomeOffice {
    /** IRS simplified method: $5/sq ft, capped at 300 sq ft ($1,500 max). */
    const val RATE_DOLLARS_PER_SQFT = 5
    const val MAX_SQFT = 300

    fun deduction(sqFt: Int): Long = (sqFt.coerceAtMost(MAX_SQFT) * RATE_DOLLARS_PER_SQFT).toLong()
}
