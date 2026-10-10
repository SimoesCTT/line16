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
import org.junit.Test

class TaxMathTest {

    // ---------- FederalTax: single (2025) ----------

    @Test fun single_zero() = assertThat(f(0, FilingStatus.SINGLE)).isEqualTo(0L)

    @Test fun single_below_std_deduction() = assertThat(f(15_750, FilingStatus.SINGLE)).isEqualTo(0L)

    @Test fun single_exactly_10pct_ceiling() {
        // taxable 11,925 → 1,192.50 → 1,193
        assertThat(f(15_750 + 11_925, FilingStatus.SINGLE)).isEqualTo(1_193L)
    }

    @Test fun single_exactly_12pct_ceiling() {
        // taxable 48,475: 1,192.50 + (48,475-11,925)*0.12
        // = 1,192.50 + 4,386.00 = 5,578.50 → 5,579
        assertThat(f(15_750 + 48_475, FilingStatus.SINGLE)).isEqualTo(5_579L)
    }

    @Test fun single_exactly_22pct_ceiling() {
        // taxable 103,350: 5,578.50 + (103,350-48,475)*0.22
        // = 5,578.50 + 12,072.50 = 17,651.00 → 17,651
        assertThat(f(15_750 + 103_350, FilingStatus.SINGLE)).isEqualTo(17_651L)
    }

    @Test fun single_exactly_24pct_ceiling() {
        // taxable 197,300: 17,651 + (197,300-103,350)*0.24
        // = 17,651 + 22,548 = 40,199
        assertThat(f(15_750 + 197_300, FilingStatus.SINGLE)).isEqualTo(40_199L)
    }

    @Test fun single_exactly_32pct_ceiling() {
        // taxable 250,525: 40,199 + (250,525-197,300)*0.32
        // = 40,199 + 17,032 = 57,231
        assertThat(f(15_750 + 250_525, FilingStatus.SINGLE)).isEqualTo(57_231L)
    }

    @Test fun single_exactly_35pct_ceiling() {
        // taxable 626,350: 57,231 + (626,350-250,525)*0.35
        // = 57,231 + 131,538.75 = 188,769.75 → 188,770
        assertThat(f(15_750 + 626_350, FilingStatus.SINGLE)).isEqualTo(188_770L)
    }

    // ---------- FederalTax: MFJ (2025) ----------

    @Test fun mfj_zero() = assertThat(f(0, FilingStatus.MFJ)).isEqualTo(0L)

    @Test fun mfj_exactly_10pct_ceiling() {
        // taxable 23,850 → 2,385
        assertThat(f(31_500 + 23_850, FilingStatus.MFJ)).isEqualTo(2_385L)
    }

    // ---------- FederalTax: HoH (2025) ----------

    @Test fun hoh_zero() = assertThat(f(0, FilingStatus.HOH)).isEqualTo(0L)

    @Test fun hoh_exactly_10pct_ceiling() {
        // taxable 17,000 → 1,700
        assertThat(f(23_625 + 17_000, FilingStatus.HOH)).isEqualTo(1_700L)
    }

    // ---------- Self-employment tax (2025 SS wage base = 176,100) ----------

    @Test fun se_zero() = assertThat(SelfEmploymentTax.compute(0).total).isEqualTo(0L)

    @Test fun se_below_ss_wage_base() {
        // 50,000 * 0.9235 = 46,175
        // SS: 46,175 * 0.124 = 5,725.70 → 5,726
        // Medicare: 46,175 * 0.029 = 1,339.075 → 1,339
        // total = 7,065
        val r = SelfEmploymentTax.compute(50_000)
        assertThat(r.ssTax).isEqualTo(5_726L)
        assertThat(r.medicareTax).isEqualTo(1_339L)
        assertThat(r.total).isEqualTo(7_065L)
        assertThat(r.deductibleHalf).isEqualTo(3_532L)
    }

    @Test fun se_caps_at_ss_wage_base() {
        val r = SelfEmploymentTax.compute(500_000)
        // SS caps at 176,100 * 0.124 = 21,836.40 → 21,836
        assertThat(r.ssTax).isEqualTo(21_836L)
    }

    @Test fun se_wages_already_exceed_ss_base() {
        // wages 200,000 > 176,100 → no SS on Schedule C
        val r = SelfEmploymentTax.compute(15_040, otherWages = 200_000)
        assertThat(r.ssTax).isEqualTo(0L)
        assertThat(r.medicareTax).isEqualTo(403L)
    }

    // ---------- Mileage / Home office ----------

    @Test fun mileage_zero() = assertThat(Mileage.deduction(0)).isEqualTo(0L)

    @Test fun mileage_1000() = assertThat(Mileage.deduction(1_000)).isEqualTo(700L)

    @Test fun mileage_fractional_rounding() = assertThat(Mileage.deduction(17)).isEqualTo(12L)

    @Test fun home_office_below_cap() = assertThat(HomeOffice.deduction(100)).isEqualTo(500L)

    @Test fun home_office_at_cap() = assertThat(HomeOffice.deduction(300)).isEqualTo(1_500L)

    @Test fun home_office_over_cap() = assertThat(HomeOffice.deduction(500)).isEqualTo(1_500L)

    // ---------- Full pipeline ----------

    @Test fun single_w2_50k() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            w2s = listOf(W2("Acme", wages = 50_000, federalWithholding = 5_000)),
        )
        val res = ReturnCalculator.compute(r)
        assertThat(res.agi).isEqualTo(50_000L)
        assertThat(res.taxableIncome).isEqualTo(34_250L)
        // 10% on 11,925 = 1,192.50; 12% on 22,325 = 2,679
        // total = 3,871.50 → 3,872
        assertThat(res.federalIncomeTax).isEqualTo(3_872L)
        assertThat(res.selfEmploymentTax).isEqualTo(0L)
        assertThat(res.refundOrOwed).isEqualTo(1_128L)
    }

    @Test fun zero_return() {
        val res = ReturnCalculator.compute(TaxReturn())
        assertThat(res.totalTax).isEqualTo(0L)
        assertThat(res.refundOrOwed).isEqualTo(0L)
        assertThat(res.effectiveRate).isEqualTo(0.0)
    }

    @Test fun freelancer_50k() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            scheduleC = ScheduleC(grossReceipts = 60_000, supplies = 5_000, advertising = 5_000),
        )
        val res = ReturnCalculator.compute(r)
        assertThat(res.totalIncome).isEqualTo(50_000L)
        assertThat(res.selfEmploymentTax).isEqualTo(7_065L)
        assertThat(res.agi).isEqualTo(46_468L)
    }

    @Test fun mfj_200k_wages_15k_schc() {
        // The big scenario, now with 2025 constants
        // wages 200,050, schC net 15,040, mileage 1000, home office 300sqft
        val r = TaxReturn(
            status = FilingStatus.MFJ,
            w2s = listOf(W2("Employer", wages = 200_050, federalWithholding = 12_000)),
            scheduleC = ScheduleC(grossReceipts = 19_440, supplies = 2_200),
            businessMiles = 1_000,
            homeOfficeSqFt = 300,
        )
        val res = ReturnCalculator.compute(r)
        // SE tax: wages 200,050 > 176,100 → no SS on Sch C
        // Medicare only: 15,040 * 0.9235 * 2.9% = 402.79 → 403
        assertThat(res.selfEmploymentTax).isEqualTo(403L)
        // Half SE = 201; AGI = 215,090 - 201 = 214,889
        assertThat(res.agi).isEqualTo(214_889L)
        // Taxable = 214,889 - 31,500 = 183,389
        assertThat(res.taxableIncome).isEqualTo(183_389L)
        // Tax: 10% on 23,850 = 2,385
        //      12% on 73,100 = 8,772
        //      22% on (183,389-96,950) = 22% on 86,439 = 19,016.58
        //      total = 30,173.58 → 30,174
        assertThat(res.federalIncomeTax).isEqualTo(30_174L)
        assertThat(res.totalTax).isEqualTo(30_577L)
        assertThat(res.refundOrOwed).isEqualTo(-18_577L)
    }

    private fun f(grossIncome: Long, status: FilingStatus): Long {
        return FederalTax.compute(
            taxableIncome = grossIncome,
            standardDeduction = status.standardDeduction(),
            brackets = status.brackets(),
        )
    }
}
