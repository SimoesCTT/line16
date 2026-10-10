package io.github.yourhandle.line16.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.yourhandle.line16.data.Line16Database
import io.github.yourhandle.line16.data.ReturnRepository
import io.github.yourhandle.line16.domain.calc.ReturnCalculator
import io.github.yourhandle.line16.domain.calc.TaxResult
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.domain.model.ScheduleC
import io.github.yourhandle.line16.domain.model.TaxReturn
import io.github.yourhandle.line16.domain.model.W2
import io.github.yourhandle.line16.domain.year.TaxYear2026
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReturnViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ReturnRepository(Line16Database.get(app).returnDao())

    private val _return = MutableStateFlow(TaxReturn())
    val current: StateFlow<TaxReturn> = _return.asStateFlow()

    private val _result = MutableStateFlow(ReturnCalculator.compute(TaxReturn()))
    val result: StateFlow<TaxResult> = _result.asStateFlow()

    init {
        viewModelScope.launch {
            _return.value = repo.load(TaxYear2026.YEAR)
            recalc()
        }
    }

    fun setStatus(s: FilingStatus) = update { it.copy(status = s) }

    fun setTaxpayer(t: io.github.yourhandle.line16.domain.model.TaxpayerInfo) = update { it.copy(taxpayer = t) }

    fun setW2(index: Int, w: W2) = update { r ->
        val list = r.w2s.toMutableList()
        if (index < list.size) list[index] = w else list.add(w)
        r.copy(w2s = list)
    }

    fun addW2() = update { it.copy(w2s = it.w2s + W2()) }
    fun removeW2(index: Int) = update { r ->
        r.copy(w2s = r.w2s.filterIndexed { i, _ -> i != index })
    }

    fun setScheduleC(sc: ScheduleC) = update { it.copy(scheduleC = sc) }
    fun setBusinessMiles(n: Int) = update { it.copy(businessMiles = n) }
    fun setHomeOfficeSqFt(n: Int) = update { it.copy(homeOfficeSqFt = n) }

    private fun update(f: (TaxReturn) -> TaxReturn) {
        val next = f(_return.value)
        _return.value = next
        recalc()
        viewModelScope.launch { repo.save(TaxYear2026.YEAR, next) }
    }

    private fun recalc() {
        _result.value = ReturnCalculator.compute(_return.value)
    }
}
