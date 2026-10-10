package com.simoesctt.line16.domain.model

data class Dependent(
    val firstName: String = "",
    val lastName: String = "",
    val ssn: String = "",
    val relationship: String = "",
    val qualifiesForChildTaxCredit: Boolean = false,
    val livesWithYou: Boolean = true,
)

data class Dependents(val list: List<Dependent> = emptyList()) {
    val qualifyingChildren: Int get() = list.count { it.qualifiesForChildTaxCredit }
}
