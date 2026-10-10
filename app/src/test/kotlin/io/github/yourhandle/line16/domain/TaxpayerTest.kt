package io.github.yourhandle.line16.domain

import com.google.common.truth.Truth.assertThat
import io.github.yourhandle.line16.domain.calc.ReturnCalculator
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.domain.model.TaxReturn
import io.github.yourhandle.line16.domain.model.TaxpayerInfo
import io.github.yourhandle.line16.domain.model.W2
import org.junit.Test

class TaxpayerTest {

    @Test fun fullName_joins_non_blank_parts() {
        assertThat(TaxpayerInfo("Jane", "Q", "Doe").fullName).isEqualTo("Jane Q Doe")
    }

    @Test fun fullName_skips_blank_middle_initial() {
        assertThat(TaxpayerInfo("Jane", "", "Doe").fullName).isEqualTo("Jane Doe")
    }

    @Test fun spouseFullName() {
        val tp = TaxpayerInfo(spouseFirstName = "John", spouseMiddleInitial = "R", spouseLastName = "Doe")
        assertThat(tp.spouseFullName).isEqualTo("John R Doe")
    }

    @Test fun cityStateZip_formats() {
        assertThat(TaxpayerInfo(city = "Austin", state = "TX", zip = "78701").cityStateZip)
            .isEqualTo("Austin, TX, 78701")
    }

    @Test fun taxpayer_info_does_not_change_tax() {
        val base = TaxReturn(
            status = FilingStatus.SINGLE,
            w2s = listOf(W2("Acme", wages = 50_000, federalWithholding = 5_000)),
        )
        val withInfo = base.copy(
            taxpayer = TaxpayerInfo(
                firstName = "Jane", middleInitial = "Q", lastName = "Doe",
                ssn = "123-45-6789", address = "1 Main St", city = "Austin",
                state = "TX", zip = "78701",
            ),
        )
        val a = ReturnCalculator.compute(base)
        val b = ReturnCalculator.compute(withInfo)
        assertThat(b.federalIncomeTax).isEqualTo(a.federalIncomeTax)
        assertThat(b.totalTax).isEqualTo(a.totalTax)
    }
}
