package com.simoesctt.line16.domain.model

data class TaxpayerInfo(
    val firstName: String = "",
    val middleInitial: String = "",
    val lastName: String = "",
    val ssn: String = "",
    val spouseFirstName: String = "",
    val spouseMiddleInitial: String = "",
    val spouseLastName: String = "",
    val spouseSsn: String = "",
    val address: String = "",
    val apt: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = "",
) {
    val fullName: String get() = listOf(firstName, middleInitial, lastName)
        .filter { it.isNotBlank() }.joinToString(" ")
    val spouseFullName: String get() = listOf(spouseFirstName, spouseMiddleInitial, spouseLastName)
        .filter { it.isNotBlank() }.joinToString(" ")
    val cityStateZip: String
        get() = listOf(city, state, zip).filter { it.isNotBlank() }.joinToString(", ")
}
