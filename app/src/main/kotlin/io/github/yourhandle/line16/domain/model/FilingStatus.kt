package io.github.yourhandle.line16.domain.model

import io.github.yourhandle.line16.domain.year.Bracket
import io.github.yourhandle.line16.domain.year.TaxYear2026

enum class FilingStatus {
    SINGLE, MFJ, HOH;

    fun standardDeduction(): Long = when (this) {
        SINGLE -> TaxYear2026.STANDARD_DEDUCTION_SINGLE
        MFJ -> TaxYear2026.STANDARD_DEDUCTION_MFJ
        HOH -> TaxYear2026.STANDARD_DEDUCTION_HOH
    }

    fun brackets(): List<Bracket> = when (this) {
        SINGLE -> TaxYear2026.SINGLE_BRACKETS
        MFJ -> TaxYear2026.MFJ_BRACKETS
        HOH -> TaxYear2026.HOH_BRACKETS
    }
}
