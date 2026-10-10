package io.github.yourhandle.line16.ui.screens.filingstatus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.ui.ReturnViewModel

@Composable
fun FilingStatusScreen(vm: ReturnViewModel) {
    val current by vm.current.collectAsState()
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Filing status", style = MaterialTheme.typography.headlineSmall)
        Text("Pick the one that matches your situation on Dec 31.", style = MaterialTheme.typography.bodyMedium)
        FilingStatus.entries.forEach { status ->
            Row(
                Modifier.fillMaxWidth().selectable(selected = current.status == status,
                    onClick = { vm.setStatus(status) }).padding(vertical = 8.dp),
            ) {
                RadioButton(selected = current.status == status, onClick = { vm.setStatus(status) })
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(status.label(), style = MaterialTheme.typography.bodyLarge)
                    Text("Standard deduction: $%,d".format(status.standardDeduction()),
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun FilingStatus.label(): String = when (this) {
    FilingStatus.SINGLE -> "Single"
    FilingStatus.MFJ -> "Married filing jointly"
    FilingStatus.HOH -> "Head of household"
}
