package io.github.yourhandle.line16.domain.calc

import io.github.yourhandle.line16.domain.year.Bracket

object FederalTax {
    fun compute(taxableIncome: Long, standardDeduction: Long, brackets: List<Bracket>): Long {
        val income = (taxableIncome - standardDeduction).coerceAtLeast(0L)
        var tax = 0.0
        var lower = 0L
        for (b in brackets) {
            val upper = b.upTo ?: Long.MAX_VALUE
            if (income <= lower) break
            val slice = (minOf(income, upper) - lower).coerceAtLeast(0L)
            tax += slice * b.rate
            lower = upper
        }
        return Math.round(tax)
    }

    fun marginalRate(taxableIncome: Long, standardDeduction: Long, brackets: List<Bracket>): Double {
        val income = (taxableIncome - standardDeduction).coerceAtLeast(0L)
        var lower = 0L
        for (b in brackets) {
            val upper = b.upTo ?: Long.MAX_VALUE
            if (income <= upper) return b.rate
            lower = upper
        }
        return brackets.last().rate
    }
}
