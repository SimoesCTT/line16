package io.github.yourhandle.line16.ui.screens.schedulec

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.yourhandle.line16.ui.ReturnViewModel
import io.github.yourhandle.line16.ui.components.IntField
import io.github.yourhandle.line16.ui.components.MoneyField
import io.github.yourhandle.line16.ui.components.toMoney

@Composable
fun ScheduleCScreen(vm: ReturnViewModel) {
    val current by vm.current.collectAsState()
    val sc = current.scheduleC
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Self-employment", style = MaterialTheme.typography.headlineSmall)
        Text("Schedule C — business income and expenses.", style = MaterialTheme.typography.bodyMedium)
        MoneyField("Gross receipts", sc.grossReceipts) { vm.setScheduleC(sc.copy(grossReceipts = it)) }
        Spacer(Modifier.height(4.dp))
        Text("Expenses", style = MaterialTheme.typography.titleMedium)
        MoneyField("Advertising", sc.advertising) { vm.setScheduleC(sc.copy(advertising = it)) }
        MoneyField("Car and truck (actual)", sc.carAndTruck) { vm.setScheduleC(sc.copy(carAndTruck = it)) }
        MoneyField("Commissions and fees", sc.commissions) { vm.setScheduleC(sc.copy(commissions = it)) }
        MoneyField("Contract labor", sc.contractLabor) { vm.setScheduleC(sc.copy(contractLabor = it)) }
        MoneyField("Insurance", sc.insurance) { vm.setScheduleC(sc.copy(insurance = it)) }
        MoneyField("Interest", sc.interest) { vm.setScheduleC(sc.copy(interest = it)) }
        MoneyField("Legal and professional", sc.legal) { vm.setScheduleC(sc.copy(legal = it)) }
        MoneyField("Office expense", sc.officeExpense) { vm.setScheduleC(sc.copy(officeExpense = it)) }
        MoneyField("Rent or lease", sc.rentLease) { vm.setScheduleC(sc.copy(rentLease = it)) }
        MoneyField("Repairs and maintenance", sc.repairs) { vm.setScheduleC(sc.copy(repairs = it)) }
        MoneyField("Supplies", sc.supplies) { vm.setScheduleC(sc.copy(supplies = it)) }
        MoneyField("Taxes and licenses", sc.taxes) { vm.setScheduleC(sc.copy(taxes = it)) }
        MoneyField("Travel", sc.travel) { vm.setScheduleC(sc.copy(travel = it)) }
        MoneyField("Meals (enter full amount)", sc.meals) { vm.setScheduleC(sc.copy(meals = it)) }
        MoneyField("Utilities", sc.utilities) { vm.setScheduleC(sc.copy(utilities = it)) }
        MoneyField("Other expenses", sc.otherExpenses) { vm.setScheduleC(sc.copy(otherExpenses = it)) }
        Spacer(Modifier.height(4.dp))
        Text("Mileage and home office", style = MaterialTheme.typography.titleMedium)
        IntField("Business miles driven", current.businessMiles) { vm.setBusinessMiles(it) }
        Text("Deduction: ${io.github.yourhandle.line16.domain.calc.Mileage.deduction(current.businessMiles).toMoney()}",
            style = MaterialTheme.typography.bodySmall)
        IntField("Home office square feet (max 300)", current.homeOfficeSqFt) { vm.setHomeOfficeSqFt(it) }
        Text("Deduction: ${io.github.yourhandle.line16.domain.calc.HomeOffice.deduction(current.homeOfficeSqFt).toMoney()}",
            style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total expenses"); Text(sc.totalExpenses.toMoney())
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Net profit", style = MaterialTheme.typography.titleMedium)
                    Text(sc.netProfit.toMoney(), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
