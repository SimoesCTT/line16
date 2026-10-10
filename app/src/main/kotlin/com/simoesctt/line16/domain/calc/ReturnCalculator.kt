package com.simoesctt.line16.domain.calc

import com.simoesctt.line16.domain.model.TaxReturn

data class LineItem(val form: String, val line: String, val label: String, val value: Long)

data class TaxResult(
    val totalIncome: Long,
    val adjustments: Long,
    val agi: Long,
    val standardDeduction: Long,
    val taxableIncome: Long,
    val federalIncomeTax: Long,
    val selfEmploymentTax: Long,
    val childTaxCredit: Long,
    val totalTax: Long,
    val withholding: Long,
    val refundOrOwed: Long,
    val effectiveRate: Double,
    val marginalRate: Double,
    val lines: List<LineItem>,
)

object ReturnCalculator {

    fun compute(r: TaxReturn): TaxResult {
        val wages = r.totalWages

        val mileage = Mileage.deduction(r.businessMiles)
        val homeOfficeSimplified = HomeOffice.deduction(r.homeOfficeSqFt)
        val scheduleC = r.scheduleC.copy(
            carAndTruck = r.scheduleC.carAndTruck + mileage,
            homeOffice = r.scheduleC.homeOffice + homeOfficeSimplified,
        )
        val netProfit = scheduleC.netProfit

        val se = SelfEmploymentTax.compute(netProfit, otherWages = wages)
        val deductibleHalfSe = se.deductibleHalf

        val totalIncome = wages + netProfit
        val adjustments = deductibleHalfSe
        val agi = (totalIncome - adjustments).coerceAtLeast(0L)

        val brackets = r.status.brackets()
        val stdDeduction = r.status.standardDeduction()
        val taxableIncome = (agi - stdDeduction).coerceAtLeast(0L)

        val federalTax = FederalTax.compute(
            taxableIncome = agi,
            standardDeduction = stdDeduction,
            brackets = brackets,
        )

        val ctc = computeCtc(r, agi)
        val totalTax = (federalTax + se.total - ctc).coerceAtLeast(0L)
        val withholding = r.totalWithholding
        val refundOrOwed = withholding - totalTax

        val effectiveRate = if (agi > 0) totalTax.toDouble() / agi else 0.0
        val marginalRate = FederalTax.marginalRate(agi, stdDeduction, brackets)

        val lines = buildList {
            add(LineItem("1040", "1", "W-2 wages", wages))
            if (netProfit > 0) add(LineItem("Sch C", "31", "Net profit", netProfit))
            add(LineItem("1040", "9", "Total income", totalIncome))
            if (adjustments > 0) add(LineItem("Sch 1", "15", "Deductible half of SE tax", adjustments))
            add(LineItem("1040", "11", "Adjusted gross income", agi))
            add(LineItem("1040", "12", "Standard deduction", stdDeduction))
            add(LineItem("1040", "15", "Taxable income", taxableIncome))
            add(LineItem("1040", "16", "Federal income tax", federalTax))
            if (ctc > 0) add(LineItem("1040", "19", "Child tax credit", ctc))
            if (se.total > 0) add(LineItem("Sch 2", "4", "Self-employment tax", se.total))
            add(LineItem("1040", "24", "Total tax", totalTax))
            add(LineItem("1040", "25a", "Federal withholding", withholding))
            add(LineItem("1040", "34/37", if (refundOrOwed >= 0) "Refund" else "Amount owed", kotlin.math.abs(refundOrOwed)))
        }

        return TaxResult(
            totalIncome = totalIncome,
            adjustments = adjustments,
            agi = agi,
            standardDeduction = stdDeduction,
            taxableIncome = taxableIncome,
            federalIncomeTax = federalTax,
            selfEmploymentTax = se.total,
            childTaxCredit = ctc,
            totalTax = totalTax,
            withholding = withholding,
            refundOrOwed = refundOrOwed,
            effectiveRate = effectiveRate,
            marginalRate = marginalRate,
            lines = lines,
        )
    }

    private fun computeCtc(r: TaxReturn, agi: Long): Long {
        val qualifying = r.dependents.count { it.qualifiesForChildTaxCredit }
        if (qualifying == 0) return 0L
        var credit = qualifying * com.simoesctt.line16.domain.year.TaxYear2025.CTC_PER_CHILD
        val threshold = when (r.status) {
            com.simoesctt.line16.domain.model.FilingStatus.MFJ ->
                com.simoesctt.line16.domain.year.TaxYear2025.CTC_PHASEOUT_START_MFJ
            else ->
                com.simoesctt.line16.domain.year.TaxYear2025.CTC_PHASEOUT_START_SINGLE
        }
        if (agi > threshold) {
            val over = agi - threshold
            val reduction = ((over + 999) / 1000) * 50
            credit = (credit - reduction).coerceAtLeast(0L)
        }
        return credit
    }
}
