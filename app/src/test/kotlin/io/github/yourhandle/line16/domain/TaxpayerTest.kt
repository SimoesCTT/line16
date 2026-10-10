package io.github.yourhandle.line16.domain

import com.google.common.truth.Truth.assertThat
import io.github.yourhandle.line16.domain.calc.ReturnCalculator
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.domain.model.ScheduleC
import io.github.yourhandle.line16.domain.model.TaxReturn
import io.github.yourhandle.line16.domain.model.TaxpayerInfo
import io.github.yourhandle.line16.domain.model.W2
import org.junit.Test

class TaxpayerTest {

    @Test fun fullName_joins_non_blank_parts() {
        val tp = TaxpayerInfo(firstName = "Jane", middleInitial = "Q", lastName = "Doe")
        assertThat(tp.fullName).isEqualTo("Jane Q Doe")
    }

    @Test fun fullName_skips_blank_middle_initial() {
        val tp = TaxpayerInfo(firstName = "Jane", middleInitial = "", lastName = "Doe")
        assertThat(tp.fullName).isEqualTo("Jane Doe")
    }

    @Test fun fullName_handles_only_last_name() {
        assertThat(TaxpayerInfo(lastName = "Doe").fullName).isEqualTo("Doe")
    }

    @Test fun spouseFullName() {
        val tp = TaxpayerInfo(
            spouseFirstName = "John",
            spouseMiddleInitial = "R",
            spouseLastName = "Doe",
        )
        assertThat(tp.spouseFullName).isEqualTo("John R Doe")
    }

    @Test fun cityStateZip_formats() {
        val tp = TaxpayerInfo(city = "Austin", state = "TX", zip = "78701")
        assertThat(tp.cityStateZip).isEqualTo("Austin, TX, 78701")
    }

    @Test fun cityStateZip_skips_blanks() {
        val tp = TaxpayerInfo(city = "Austin", state = "TX", zip = "")
        assertThat(tp.cityStateZip).isEqualTo("Austin, TX")
    }

    // ---- Taxpayer info does not affect the math ----

    @Test fun taxpayer_info_does_not_change_tax() {
        val base = TaxReturn(
            status = FilingStatus.SINGLE,
            w2s = listOf(W2("Acme", wages = 50_000, federalWithholding = 5_000)),
        )
        val withInfo = base.copy(
            taxpayer = TaxpayerInfo(
                firstName = "Jane",
                middleInitial = "Q",
                lastName = "Doe",
                ssn = "123-45-6789",
                address = "123 Main St",
                apt = "4B",
                city = "Austin",
                state = "TX",
                zip = "78701",
            ),
        )
        val a = ReturnCalculator.compute(base)
        val b = ReturnCalculator.compute(withInfo)
        assertThat(b.federalIncomeTax).isEqualTo(a.federalIncomeTax)
        assertThat(b.totalTax).isEqualTo(a.totalTax)
        assertThat(b.refundOrOwed).isEqualTo(a.refundOrOwed)
    }

    // ---- The exact MFJ scenario again, now with spouse info ----

    @Test fun mfj_with_full_taxpayer_info() {
        val r = TaxReturn(
            status = FilingStatus.MFJ,
            taxpayer = TaxpayerInfo(
                firstName = "Americo", lastName = "Simoes", ssn = "111-11-1111",
                spouseFirstName = "Maria", spouseLastName = "Simoes", spouseSsn = "222-22-2222",
                address = "1 Main St", city = "Austin", state = "TX", zip = "78701",
            ),
            w2s = listOf(W2("Employer", wages = 200_050, federalWithholding = 12_000)),
            scheduleC = ScheduleC(grossReceipts = 19_440, supplies = 2_200),
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

    // ---- Regression: the 41 tests that already existed still pass ----

    @Test fun regression_single_50k_w2() {
        val r = TaxReturn(
            status = FilingStatus.SINGLE,
            w2s = listOf(W2("Acme", wages = 50_000, federalWithholding = 5_000)),
        )
        val res = ReturnCalculator.compute(r)
        assertThat(res.federalIncomeTax).isEqualTo(3_820L)
        assertThat(res.refundOrOwed).isEqualTo(1_180L)
    }
}
