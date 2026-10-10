package io.github.yourhandle.line16.domain.model

import io.github.yourhandle.line16.domain.year.Bracket
import io.github.yourhandle.line16.domain.year.TaxYear2025

enum class FilingStatus {
    SINGLE, MFJ, HOH;

    fun standardDeduction(): Long = when (this) {
        SINGLE -> TaxYear2025.STANDARD_DEDUCTION_SINGLE
        MFJ -> TaxYear2025.STANDARD_DEDUCTION_MFJ
        HOH -> TaxYear2025.STANDARD_DEDUCTION_HOH
    }

    fun brackets(): List<Bracket> = when (this) {
        SINGLE -> TaxYear2025.SINGLE_BRACKETS
        MFJ -> TaxYear2025.MFJ_BRACKETS
        HOH -> TaxYear2025.HOH_BRACKETS
    }
}
