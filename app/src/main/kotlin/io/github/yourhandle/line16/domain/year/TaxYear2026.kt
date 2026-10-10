package io.github.yourhandle.line16.domain.year

data class Bracket(val rate: Double, val upTo: Long?)

object TaxYear2026 {
    const val YEAR = 2026

    const val STANDARD_DEDUCTION_SINGLE = 16_100L
    const val STANDARD_DEDUCTION_MFJ = 32_200L
    const val STANDARD_DEDUCTION_HOH = 24_150L

    val SINGLE_BRACKETS = listOf(
        Bracket(0.10, 12_400),
        Bracket(0.12, 50_400),
        Bracket(0.22, 105_700),
        Bracket(0.24, 201_775),
        Bracket(0.32, 256_225),
        Bracket(0.35, 640_600),
        Bracket(0.37, null),
    )

    // MFJ = double the single brackets for the first four, then slightly less than double
    // for the top three. Numbers below use the "double the single thresholds" rule for
    // the 10/12/22/24 bands and the published 2026 MFJ figures for 32/35/37.
    val MFJ_BRACKETS = listOf(
        Bracket(0.10, 24_800),
        Bracket(0.12, 100_800),
        Bracket(0.22, 211_400),
        Bracket(0.24, 403_550),
        Bracket(0.32, 512_450),
        Bracket(0.35, 768_700),
        Bracket(0.37, null),
    )

    // HoH brackets sit between single and MFJ. Approximate; verify against IRS Rev. Proc.
    // when the 2026 tables publish.
    val HOH_BRACKETS = listOf(
        Bracket(0.10, 17_700),
        Bracket(0.12, 67_450),
        Bracket(0.22, 105_700),
        Bracket(0.24, 201_775),
        Bracket(0.32, 256_200),
        Bracket(0.35, 640_600),
        Bracket(0.37, null),
    )

    const val SE_TAX_RATE = 0.153
    const val SE_NET_EARNINGS_FACTOR = 0.9235
    const val SS_WAGE_BASE = 184_500L
    const val SS_RATE = 0.124
    const val MEDICARE_RATE = 0.029
    const val ADDL_MEDICARE_RATE = 0.009
    const val ADDL_MEDICARE_THRESHOLD_SINGLE = 200_000L
    const val ADDL_MEDICARE_THRESHOLD_MFJ = 250_000L
}
