package io.github.yourhandle.line16.ui.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("About Line 16", style = MaterialTheme.typography.headlineSmall)
        Text("Line 16 is a free, offline US federal tax calculator for the 2026 tax year. " +
            "It computes your income tax and self-employment tax and shows you what to put on each line of your 1040.",
            style = MaterialTheme.typography.bodyMedium)
        Text("Privacy", style = MaterialTheme.typography.titleMedium)
        Text("All data stays on this device. No internet permission. No accounts. No tracking.",
            style = MaterialTheme.typography.bodyMedium)
        Text("Not tax advice", style = MaterialTheme.typography.titleMedium)
        Text("Line 16 is software, not a CPA. It does not file your return and does not give advice.",
            style = MaterialTheme.typography.bodyMedium)
        Text("Source and license", style = MaterialTheme.typography.titleMedium)
        Text("Licensed under GPL-3.0-or-later.", style = MaterialTheme.typography.bodyMedium)
        Text("Support", style = MaterialTheme.typography.titleMedium)
        Text("Donations welcome via PayPal: your-email@example.com. Optional, never required.",
            style = MaterialTheme.typography.bodyMedium)
    }
}
