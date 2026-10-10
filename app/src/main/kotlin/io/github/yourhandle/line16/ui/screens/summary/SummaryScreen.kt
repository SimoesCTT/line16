package io.github.yourhandle.line16.ui.screens.summary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.yourhandle.line16.export.PdfSharer
import io.github.yourhandle.line16.export.ReturnPdf
import io.github.yourhandle.line16.ui.ReturnViewModel
import io.github.yourhandle.line16.ui.components.toMoney
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SummaryScreen(vm: ReturnViewModel) {
    val result by vm.result.collectAsState()
    val current by vm.current.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("1040 summary", style = MaterialTheme.typography.headlineSmall)
        Text("What to put on each line.", style = MaterialTheme.typography.bodyMedium)

        Button(
            onClick = {
                scope.launch {
                    val file = withContext(Dispatchers.IO) {
                        ReturnPdf.write(context, current, result)
                    }
                    PdfSharer.share(context, file)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Export PDF summary") }

        Spacer(Modifier.height(4.dp))

        result.lines.forEach { line ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("${line.form} · Line ${line.line}", style = MaterialTheme.typography.labelSmall)
                        Text(line.label, style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(line.value.toMoney(), style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Rates", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Effective rate")
                    Text("%.2f%%".format(result.effectiveRate * 100))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Marginal rate")
                    Text("%.0f%%".format(result.marginalRate * 100))
                }
            }
        }
    }
}
