package io.github.yourhandle.line16.ui.screens.w2

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
import io.github.yourhandle.line16.ui.components.MoneyField

@Composable
fun W2Screen(vm: ReturnViewModel) {
    val current by vm.current.collectAsState()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("W-2 income", style = MaterialTheme.typography.headlineSmall)
        Text("One entry per W-2 you received.", style = MaterialTheme.typography.bodyMedium)
        current.w2s.forEachIndexed { i, w2 ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("W-2 #${i + 1}", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = w2.employer,
                        onValueChange = { vm.setW2(i, w2.copy(employer = it)) },
                        label = { Text("Employer (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    MoneyField("Box 1 — Wages", w2.wages) { vm.setW2(i, w2.copy(wages = it)) }
                    MoneyField("Box 2 — Federal withholding", w2.federalWithholding) {
                        vm.setW2(i, w2.copy(federalWithholding = it))
                    }
                    TextButton({ vm.removeW2(i) }) { Text("Remove") }
                }
            }
        }
        Button({ vm.addW2() }, Modifier.fillMaxWidth()) { Text("Add W-2") }
    }
}
