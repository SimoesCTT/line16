package io.github.yourhandle.line16.domain

import com.google.common.truth.Truth.assertThat
import io.github.yourhandle.line16.domain.calc.FederalTax
import io.github.yourhandle.line16.domain.calc.HomeOffice
import io.github.yourhandle.line16.domain.calc.Mileage
import io.github.yourhandle.line16.domain.calc.ReturnCalculator
import io.github.yourhandle.line16.domain.calc.SelfEmploymentTax
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.domain.model.ScheduleC
import io.github.yourhandle.line16.domain.model.TaxReturn
import io.github.yourhandle.line16.domain.model.W2
import io.github.yourhandle.line16.domain.year.TaxYear2026
import org.junit.Test

class TaxMathTest {

    // ---------- FederalTax: single ----------

    @Test fun single_zero() {
        assertThat(f(0, FilingStatus.SINGLE)).isEqualTo(0L)
    }

    @Test fun single_below_std_deduction() {
        assertThat(f(16_100, FilingStatus.SINGLE)).isEqualTo(0L)
    }

    @Test fun single_first_dollar_into_10pct() {
        // taxable = 1 → 10% → 0.10 → rounds to 0
        assertThat(f(16_101, FilingStatus.SINGLE)).isEqualTo(0L)
    }

    @Test fun single_exactly_10pct_ceiling() {
        // taxable = 12,400 → 1,240
        assertThat(f(16_100 + 12_400, FilingStatus.SINGLE)).isEqualTo(1_240L)
    }

    @Test fun single_one_dollar_over_10pct() {
        // taxable = 12,401 → 1,240 + 0.12 → rounds to 1,240
        assertThat(f(16_100 + 12_401, FilingStatus.SINGLE)).isEqualTo(1_240L)
    }

    @Test fun single_exactly_12pct_ceiling() {
        // taxable = 50,400 → 1,240 + 38,000*0.12 = 1,240 + 4,560 = 5,800
        assertThat(f(16_100 + 50_400, FilingStatus.SINGLE)).isEqualTo(5_800L)
    }

    @Test fun single_exactly_22pct_ceiling() {
        // taxable = 105,700
        // 1,240 + 4,560 + (105,700-50,400)*0.22
        // = 1,240 + 4,560 + 12,166 = 17,966
        assertThat(f(16_100 + 105_700, FilingStatus.SINGLE)).isEqualTo(17_966L)
    }

    @Test fun single_exactly_24pct_ceiling() {
        // taxable = 201,775
        // 17,966 + (201,775-105,700)*0.24
        // = 17,966 + 23,058 = 41,024
        assertThat(f(16_100 + 201_775, FilingStatus.SINGLE)).isEqualTo(41_024L)
    }

    @Test fun single_exactly_32pct_ceiling() {
        // taxable = 256,225
        // 41,024 + (256,225-201,775)*0.32
        // = 41,024 + 17,424 = 58,448
        assertThat(f(16_100 + 256_225, FilingStatus.SINGLE)).isEqualTo(58_448L)
    }

    @Test fun single_exactly_35pct_ceiling() {
        // taxable = 640,600
        // 58,448 + (640,600-256,225)*0.35
        // = 58,448 + 134,531.25 → 134,531
        // total = 192,979
        assertThat(f(16_100 + 640_600, FilingStatus.SINGLE)).isEqualTo(192_979L)
    }

    // ---------- FederalTax: MFJ ----------

    @Test fun mfj_zero() {
        assertThat(f(0, FilingStatus.MFJ)).isEqualTo(0L)
    }

    @Test fun mfj_exactly_10pct_ceiling() {
        // taxable = 24,800 → 2,480
        assertThat(f(32_200 + 24_800, FilingStatus.MFJ)).isEqualTo(2_480L)
    }

    @Test fun mfj_real_world_200k_wages_15k_schc() {
        // The scenario from the PDF:
        // wages 200,050 + schC net 15,040 = total 215,090
        // agi 214,889 (after 201 half-SE)
        // taxable 182,689 (after 32,200 MFJ std deduction)
        // federal tax = 29,616
        // SE = 403, total = 30,019, owed 18,019
        val r = TaxReturn(
            status = FilingStatus.MFJ,
            w2s = listOf(W2("Employer", wages = 200_050, federalWithholding = 12_000)),
            scheduleC = ScheduleC(grossReceipts = 17_240 + 2_200, supplies = 2_200),
            businessMiles = 1_000,
            homeOfficeSqFt = 300,
        )
        val res = ReturnCalculator.compute(r)
        assertThat(res.agi).isEqualTo(214_889L)
        assertThat(res.taxableIncome).isEqualTo(182_689L)
        assertThat(res.federalIncomeTax).isEqualTo(29_616L)
        assertThat(res.selfEmploymentTax).isEqualTo(403L)
        assertThat(res.totalTax).isEqualTo(30_019L)
        assertThat(res.refundOrOwed).isEqualTo(-18_019L)
    }

    // ---------- FederalTax: HoH ----------

    @Test fun hoh_zero() {
        assertThat(f(0, FilingStatus.HOH)).isEqualTo(0L)
    }

    @Test fun hoh_exactly_10pct_ceiling() {
        // taxable = 17,700 → 1,770
        assertThat(f(24_150 + 17_700, FilingStatus.HOH)).isEqualTo(1_770L)
    }

    // ---------- Self-employment tax ----------

    @Test fun se_zero() {
        assertThat(SelfEmploymentTax.compute(0).total).isEqualTo(0L)
    }

    @Test fun se_below_ss_wage_base() {
        // 50,000 * 0.9235 = 46,175
        // SS: 46,175 * 12.4% = 5,725.70 → 5,726
        // Medicare: 46,175 * 2.9% = 1,339.075 → 1,339
        // total 7,065
        val r = SelfEmploymentTax.compute(50_000)
        assertThat(r.ssTax).isEqualTo(5_726L)
        assertThat(r.medicareTax).isEqualTo(1_339L)
        assertThat(r.total).isEqualTo(7_065L)
        assertThat(r.deductibleHalf).isEqualTo(3_532L)
    }

    @Test fun se_caps_at_ss_wage_base() {
        val r = SelfEmploymentTax.compute(500_000)
        // SS should cap at 184,500 * 12.4% = 22,878
        assertThat(r.ssTax).isEqualTo(22_878L)
    }

    @Test fun se_wages_already_exceed_ss_base() {
        // wages 200,050 > 184,500 → no SS on Schedule C
        val r = SelfEmploymentTax.compute(15_040, otherWages = 200_050)
        assertThat(r.ssTax).isEqualTo(0L)
        // Medicare only: 15,040 * 0.9235 * 2.9% = 402.79 → 403
        assertThat(r.medicareTax).isEqualTo(403L)
        assertThat(r.total).isEqualTo(403L)
    }

    // ---------- Mileage ----------

    @Test fun mileage_zero() {
        assertThat(Mileage.deduction(0)).isEqualTo(0L)
    }

    @Test fun mileage_1000() {
        assertThat(Mileage.deduction(1_000)).isEqualTo(700L)
    }

    @Test fun mileage_fractional_rounding() {
        // 17 miles * 0.70 = 11.90 → 12
        assertThat(Mileage.deduction(17)).isEqualTo(12L)
    }

    // ---------- Home office ----------

    @Test fun home_office_below_cap() {
        assertThat(HomeOffice.deduction(100)).isEqualTo(500L)
    }

    @Test fun home_office_at_cap() {
        assertThat(HomeOffice.deduction(300)).isEqualTo(1_500L)
    }

    @Test fun home_office_over_cap() {
        assertThat(HomeOffice.deduction(500)).isEqualTo(1_500L)
    }

    // ---------- Full pipeline: plain W-2, single ----------

    @Test fun single_w2_only_50k() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            w2s = listOf(W2("Acme", wages = 50_000, federalWithholding = 5_000)),
        )
        val res = ReturnCalculator.compute(r)
        assertThat(res.agi).isEqualTo(50_000L)
        assertThat(res.taxableIncome).isEqualTo(33_900L)
        assertThat(res.federalIncomeTax).isEqualTo(3_820L)
        assertThat(res.selfEmploymentTax).isEqualTo(0L)
        assertThat(res.refundOrOwed).isEqualTo(1_180L)
    }

    @Test fun single_w2_only_zero() {
        val res = ReturnCalculator.compute(TaxReturn())
        assertThat(res.totalTax).isEqualTo(0L)
        assertThat(res.refundOrOwed).isEqualTo(0L)
        assertThat(res.effectiveRate).isEqualTo(0.0)
    }

    @Test fun single_freelancer_50k() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            scheduleC = ScheduleC(grossReceipts = 60_000, supplies = 5_000, advertising = 5_000),
        )
        val res = ReturnCalculator.compute(r)
        assertThat(res.totalIncome).isEqualTo(50_000L)
        // SE: 50,000 * 0.9235 = 46,175 → SS 5,726 + Medicare 1,339 = 7,065
        assertThat(res.selfEmploymentTax).isEqualTo(7_065L)
        // Half SE = 3,532; AGI = 50,000 - 3,532 = 46,468
        assertThat(res.agi).isEqualTo(46_468L)
    }

    // ---------- helpers ----------

    private fun f(grossIncome: Long, status: FilingStatus): Long {
        return FederalTax.compute(
            taxableIncome = grossIncome,
            standardDeduction = status.standardDeduction(),
            brackets = status.brackets(),
        )
    }
}
