package io.github.yourhandle.line16.domain.model

data class TaxReturn(
    val status: FilingStatus = FilingStatus.SINGLE,
    val w2s: List<W2> = emptyList(),
    val scheduleC: ScheduleC = ScheduleC(),
    val businessMiles: Int = 0,
    val homeOfficeSqFt: Int = 0,
) {
    val totalWages: Long get() = w2s.sumOf { it.wages }
    val totalWithholding: Long get() = w2s.sumOf { it.federalWithholding }
}
