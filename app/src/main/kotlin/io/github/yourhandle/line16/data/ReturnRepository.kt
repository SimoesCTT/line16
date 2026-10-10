package io.github.yourhandle.line16.data

import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.domain.model.ScheduleC
import io.github.yourhandle.line16.domain.model.TaxReturn
import io.github.yourhandle.line16.domain.model.TaxpayerInfo
import io.github.yourhandle.line16.domain.model.W2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ReturnRepository(private val dao: ReturnDao) {

    fun observe(year: Int): Flow<TaxReturn?> = dao.observe(year).map { it?.toDomain() }

    suspend fun load(year: Int): TaxReturn = dao.get(year)?.toDomain() ?: TaxReturn()

    suspend fun save(year: Int, r: TaxReturn) = dao.upsert(r.toEntity(year))

    suspend fun clear(year: Int) = dao.delete(year)

    private fun TaxReturn.toEntity(year: Int) = ReturnEntity(
        year = year,
        status = status.name,
        taxpayerJson = JSONObject().apply {
            put("firstName", taxpayer.firstName)
            put("middleInitial", taxpayer.middleInitial)
            put("lastName", taxpayer.lastName)
            put("ssn", taxpayer.ssn)
            put("spouseFirstName", taxpayer.spouseFirstName)
            put("spouseMiddleInitial", taxpayer.spouseMiddleInitial)
            put("spouseLastName", taxpayer.spouseLastName)
            put("spouseSsn", taxpayer.spouseSsn)
            put("address", taxpayer.address)
            put("apt", taxpayer.apt)
            put("city", taxpayer.city)
            put("state", taxpayer.state)
            put("zip", taxpayer.zip)
        }.toString(),
        w2Json = JSONArray().apply {
            w2s.forEach { w ->
                put(JSONObject().apply {
                    put("employer", w.employer)
                    put("wages", w.wages)
                    put("federalWithholding", w.federalWithholding)
                })
            }
        }.toString(),
        scheduleCJson = JSONObject().apply {
            put("grossReceipts", scheduleC.grossReceipts)
            put("advertising", scheduleC.advertising)
            put("carAndTruck", scheduleC.carAndTruck)
            put("commissions", scheduleC.commissions)
            put("contractLabor", scheduleC.contractLabor)
            put("insurance", scheduleC.insurance)
            put("interest", scheduleC.interest)
            put("legal", scheduleC.legal)
            put("officeExpense", scheduleC.officeExpense)
            put("rentLease", scheduleC.rentLease)
            put("repairs", scheduleC.repairs)
            put("supplies", scheduleC.supplies)
            put("taxes", scheduleC.taxes)
            put("travel", scheduleC.travel)
            put("meals", scheduleC.meals)
            put("utilities", scheduleC.utilities)
            put("homeOffice", scheduleC.homeOffice)
            put("otherExpenses", scheduleC.otherExpenses)
        }.toString(),
        businessMiles = businessMiles,
        homeOfficeSqFt = homeOfficeSqFt,
    )

    private fun ReturnEntity.toDomain(): TaxReturn {
        val t = JSONObject(taxpayerJson)
        val tp = TaxpayerInfo(
            firstName = t.optString("firstName"),
            middleInitial = t.optString("middleInitial"),
            lastName = t.optString("lastName"),
            ssn = t.optString("ssn"),
            spouseFirstName = t.optString("spouseFirstName"),
            spouseMiddleInitial = t.optString("spouseMiddleInitial"),
            spouseLastName = t.optString("spouseLastName"),
            spouseSsn = t.optString("spouseSsn"),
            address = t.optString("address"),
            apt = t.optString("apt"),
            city = t.optString("city"),
            state = t.optString("state"),
            zip = t.optString("zip"),
        )
        val w2Arr = JSONArray(w2Json)
        val w2s = (0 until w2Arr.length()).map { i ->
            val o = w2Arr.getJSONObject(i)
            W2(
                employer = o.optString("employer"),
                wages = o.optLong("wages"),
                federalWithholding = o.optLong("federalWithholding"),
            )
        }
        val s = JSONObject(scheduleCJson)
        val sc = ScheduleC(
            grossReceipts = s.optLong("grossReceipts"),
            advertising = s.optLong("advertising"),
            carAndTruck = s.optLong("carAndTruck"),
            commissions = s.optLong("commissions"),
            contractLabor = s.optLong("contractLabor"),
            insurance = s.optLong("insurance"),
            interest = s.optLong("interest"),
            legal = s.optLong("legal"),
            officeExpense = s.optLong("officeExpense"),
            rentLease = s.optLong("rentLease"),
            repairs = s.optLong("repairs"),
            supplies = s.optLong("supplies"),
            taxes = s.optLong("taxes"),
            travel = s.optLong("travel"),
            meals = s.optLong("meals"),
            utilities = s.optLong("utilities"),
            homeOffice = s.optLong("homeOffice"),
            otherExpenses = s.optLong("otherExpenses"),
        )
        return TaxReturn(
            status = runCatching { FilingStatus.valueOf(status) }.getOrDefault(FilingStatus.SINGLE),
            taxpayer = tp,
            w2s = w2s,
            scheduleC = sc,
            businessMiles = businessMiles,
            homeOfficeSqFt = homeOfficeSqFt,
        )
    }
}
