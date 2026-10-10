package io.github.yourhandle.line16.domain

import com.google.common.truth.Truth.assertThat
import io.github.yourhandle.line16.domain.calc.SelfEmploymentTax
import org.junit.Test

class SelfEmploymentTaxTest {
    @Test fun zeroProfit() {
        assertThat(SelfEmploymentTax.compute(0).total).isEqualTo(0L)
    }

    @Test fun fiftyThousandProfit() {
        // 50,000 * 0.9235 = 46,175
        // SS: 46,175 * 12.4% = 5,725.70 → 5,726
        // Medicare: 46,175 * 2.9% = 1,339.075 → 1,339
        // total = 7,065
        val r = SelfEmploymentTax.compute(50_000)
        assertThat(r.total).isEqualTo(7_065L)
    }

    @Test fun capsSSAtWageBase() {
        val r = SelfEmploymentTax.compute(500_000)
        assertThat(r.ssTax).isEqualTo(Math.round(184_500 * 0.124))
    }
}
