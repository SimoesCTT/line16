package io.github.yourhandle.line16.domain

import com.google.common.truth.Truth.assertThat
import io.github.yourhandle.line16.domain.calc.FederalTax
import io.github.yourhandle.line16.domain.year.TaxYear2026
import org.junit.Test

class FederalTaxTest {
    @Test fun zeroIncome() {
        assertThat(FederalTax.compute(0, TaxYear2026.STANDARD_DEDUCTION_SINGLE, TaxYear2026.SINGLE_BRACKETS)).isEqualTo(0L)
    }

    @Test fun belowStandardDeduction() {
        assertThat(FederalTax.compute(10_000, TaxYear2026.STANDARD_DEDUCTION_SINGLE, TaxYear2026.SINGLE_BRACKETS)).isEqualTo(0L)
    }

    @Test fun fiftyThousandSingle() {
        // 50,000 - 16,100 = 33,900 taxable
        // 12,400 * 10% = 1,240
        // 21,500 * 12% = 2,580
        // total = 3,820
        assertThat(FederalTax.compute(50_000, TaxYear2026.STANDARD_DEDUCTION_SINGLE, TaxYear2026.SINGLE_BRACKETS)).isEqualTo(3_820L)
    }

    @Test fun exactBracketBoundary() {
        // 16,100 + 12,400 = 28,500 → all in 10% bracket
        assertThat(FederalTax.compute(28_500, TaxYear2026.STANDARD_DEDUCTION_SINGLE, TaxYear2026.SINGLE_BRACKETS)).isEqualTo(1_240L)
    }

    @Test fun highEarner() {
        // 200,000 - 16,100 = 183,900
        // 10% on 12,400 = 1,240
        // 12% on 38,000 = 4,560
        // 22% on 55,300 = 12,166
        // 24% on 78,200 = 18,768
        // total = 36,734
        assertThat(FederalTax.compute(200_000, TaxYear2026.STANDARD_DEDUCTION_SINGLE, TaxYear2026.SINGLE_BRACKETS)).isEqualTo(36_734L)
    }
}
