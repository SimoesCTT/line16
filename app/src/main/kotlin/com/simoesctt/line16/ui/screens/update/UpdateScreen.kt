package com.simoesctt.line16.ui.screens.update

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.simoesctt.line16.data.forms.FormUpdater
import com.simoesctt.line16.domain.year.TaxYear2025
import kotlinx.coroutines.launch

@Composable
fun UpdateScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var status by remember { mutableStateOf("Idle") }
    var working by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Form updates", style = MaterialTheme.typography.headlineSmall)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Why this screen exists", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Line 16 computes 2025 tax. The IRS form in the app is the 2025 1040. " +
                    "When the IRS publishes a new revision, this screen can download it. " +
                    "Downloaded files must match a SHA-256 hash pinned inside the app. " +
                    "If they don't match, the download is discarded.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Network", style = MaterialTheme.typography.titleMedium)
                Text("This is the only network call the app ever makes. It goes to " +
                    "www.irs.gov over HTTPS. Nothing you type is ever sent anywhere.",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

        Text("Current status: $status", style = MaterialTheme.typography.bodyMedium)

        Button(
            enabled = !working,
            onClick = {
                working = true
                status = "Downloading..."
                scope.launch {
                    when (val r = FormUpdater.download(context, TaxYear2025.YEAR)) {
                        is FormUpdater.Result.Success ->
                            status = "Updated. SHA-256 ${r.sha256.take(16)}..."
                        is FormUpdater.Result.HashMismatch ->
                            status = "Rejected. Hash mismatch.\nExpected ${r.expected.take(16)}...\nGot      ${r.actual.take(16)}..."
                        is FormUpdater.Result.Error ->
                            status = "Error: ${r.message}"
                    }
                    working = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Download latest IRS 1040") }
    }
}
