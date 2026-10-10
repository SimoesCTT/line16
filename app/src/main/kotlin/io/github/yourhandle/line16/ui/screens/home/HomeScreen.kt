package io.github.yourhandle.line16.ui.screens.home

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
import io.github.yourhandle.line16.ui.components.toMoney
import io.github.yourhandle.line16.ui.nav.Routes

@Composable
fun HomeScreen(vm: ReturnViewModel, onNavigate: (String) -> Unit) {
    val result by vm.result.collectAsState()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Line 16", style = MaterialTheme.typography.headlineLarge)
        Text("US federal tax — 2026 tax year", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Your current numbers", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total income"); Text(result.totalIncome.toMoney())
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total tax"); Text(result.totalTax.toMoney())
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (result.refundOrOwed >= 0) "Refund" else "Owed",
                        style = MaterialTheme.typography.titleMedium)
                    Text(result.refundOrOwed.toMoney(), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Button({ onNavigate(Routes.FILING_STATUS) }, Modifier.fillMaxWidth()) { Text("Filing status") }
        Button({ onNavigate(Routes.W2) }, Modifier.fillMaxWidth()) { Text("W-2 income") }
        Button({ onNavigate(Routes.SCHEDULE_C) }, Modifier.fillMaxWidth()) { Text("Self-employment (Schedule C)") }
        Button({ onNavigate(Routes.SUMMARY) }, Modifier.fillMaxWidth()) { Text("View 1040 summary") }
        OutlinedButton({ onNavigate(Routes.ABOUT) }, Modifier.fillMaxWidth()) { Text("About") }
    }
}
