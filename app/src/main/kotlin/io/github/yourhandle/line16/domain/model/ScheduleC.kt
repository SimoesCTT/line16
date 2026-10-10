package io.github.yourhandle.line16.domain.model

data class ScheduleC(
    val grossReceipts: Long = 0L,
    val advertising: Long = 0L,
    val carAndTruck: Long = 0L,
    val commissions: Long = 0L,
    val contractLabor: Long = 0L,
    val insurance: Long = 0L,
    val interest: Long = 0L,
    val legal: Long = 0L,
    val officeExpense: Long = 0L,
    val rentLease: Long = 0L,
    val repairs: Long = 0L,
    val supplies: Long = 0L,
    val taxes: Long = 0L,
    val travel: Long = 0L,
    val meals: Long = 0L,
    val utilities: Long = 0L,
    val homeOffice: Long = 0L,
    val otherExpenses: Long = 0L,
) {
    val totalExpenses: Long
        get() = advertising + carAndTruck + commissions + contractLabor + insurance +
                interest + legal + officeExpense + rentLease + repairs + supplies +
                taxes + travel + meals + utilities + homeOffice + otherExpenses

    val netProfit: Long get() = (grossReceipts - totalExpenses).coerceAtLeast(0L)
}
