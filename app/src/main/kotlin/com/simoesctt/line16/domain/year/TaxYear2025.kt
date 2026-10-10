package com.simoesctt.line16.domain.year

/**
 * 2025 federal tax constants, matching IRS Form 1040 (2025) and Rev. Proc. 2024-40.
 */
object TaxYear2025 {
    const val YEAR = 2025

    const val STANDARD_DEDUCTION_SINGLE = 15_750L
    const val STANDARD_DEDUCTION_MFJ = 31_500L
    const val STANDARD_DEDUCTION_HOH = 23_625L

    val SINGLE_BRACKETS = listOf(
        Bracket(0.10, 11_925),
        Bracket(0.12, 48_475),
        Bracket(0.22, 103_350),
        Bracket(0.24, 197_300),
        Bracket(0.32, 250_525),
        Bracket(0.35, 626_350),
        Bracket(0.37, null),
    )

    val MFJ_BRACKETS = listOf(
        Bracket(0.10, 23_850),
        Bracket(0.12, 96_950),
        Bracket(0.22, 206_700),
        Bracket(0.24, 394_600),
        Bracket(0.32, 501_050),
        Bracket(0.35, 751_600),
        Bracket(0.37, null),
    )

    val HOH_BRACKETS = listOf(
        Bracket(0.10, 17_000),
        Bracket(0.12, 64_850),
        Bracket(0.22, 103_350),
        Bracket(0.24, 197_300),
        Bracket(0.32, 250_500),
        Bracket(0.35, 626_350),
        Bracket(0.37, null),
    )

    const val SE_TAX_RATE = 0.153
    const val SE_NET_EARNINGS_FACTOR = 0.9235
    const val SS_WAGE_BASE = 176_100L
    const val SS_RATE = 0.124
    const val MEDICARE_RATE = 0.029
    const val ADDL_MEDICARE_RATE = 0.009
    const val ADDL_MEDICARE_THRESHOLD_SINGLE = 200_000L
    const val ADDL_MEDICARE_THRESHOLD_MFJ = 250_000L

    const val CTC_PER_CHILD = 2_000L
    const val CTC_PHASEOUT_START_SINGLE = 200_000L
    const val CTC_PHASEOUT_START_MFJ = 400_000L
}
