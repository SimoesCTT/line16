package io.github.yourhandle.line16.domain

import com.google.common.truth.Truth.assertThat
import io.github.yourhandle.line16.domain.calc.ReturnCalculator
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.domain.model.ScheduleC
import io.github.yourhandle.line16.domain.model.TaxReturn
import io.github.yourhandle.line16.domain.model.W2
import org.junit.Test

class ScenarioTest {

    @Test fun `pure W-2 single fifty k`() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            w2s = listOf(W2("Acme", wages = 50_000, federalWithholding = 5_000)),
        )
        val res = ReturnCalculator.compute(r)
        assertThat(res.federalIncomeTax).isEqualTo(3_820L)
        assertThat(res.selfEmploymentTax).isEqualTo(0L)
        assertThat(res.refundOrOwed).isEqualTo(1_180L) // 5000 - 3820
    }

    @Test fun `freelancer fifty k net`() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            scheduleC = ScheduleC(grossReceipts = 60_000, supplies = 5_000, advertising = 5_000),
        )
        val res = ReturnCalculator.compute(r)
        // net profit 50,000, SE tax ~7,065, half deductible ~3,532
        // AGI = 50,000 - 3,532 = 46,468
        // taxable = 46,468 - 16,100 = 30,368
        assertThat(res.totalIncome).isEqualTo(50_000L)
        assertThat(res.selfEmploymentTax).isGreaterThan(7_000L)
        assertThat(res.totalTax).isGreaterThan(res.selfEmploymentTax)
        assertThat(res.lines.any { it.label == "Self-employment tax" }).isTrue()
    }

    @Test fun `mileage adds to schedule C`() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            scheduleC = ScheduleC(grossReceipts = 10_000),
            businessMiles = 1_000,
        )
        val res = ReturnCalculator.compute(r)
        // 1000 miles * $0.70 = $700 deduction, net profit 9,300
        assertThat(res.totalIncome).isEqualTo(9_300L)
    }

    @Test fun `home office capped at 300 sqft`() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            scheduleC = ScheduleC(grossReceipts = 10_000),
            homeOfficeSqFt = 500,
        )
        val res = ReturnCalculator.compute(r)
        // 300 * $5 = $1,500, net profit 8,500
        assertThat(res.totalIncome).isEqualTo(8_500L)
    }

    @Test fun `bracket walker - exact boundary`() {
        // AGI 28,500 → taxable 12,400 → all at 10% = 1,240
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            w2s = listOf(W2(wages = 28_500)),
        )
        val res = ReturnCalculator.compute(r)
        assertThat(res.taxableIncome).isEqualTo(12_400L)
        assertThat(res.federalIncomeTax).isEqualTo(1_240L)
    }
}
