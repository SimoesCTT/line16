package com.simoesctt.line16.ui.screens.dependents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.simoesctt.line16.domain.model.Dependent
import com.simoesctt.line16.ui.ReturnViewModel

@Composable
fun DependentsScreen(vm: ReturnViewModel) {
    val current by vm.current.collectAsState()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Dependents", style = MaterialTheme.typography.headlineSmall)
        Text("Up to 4 dependents. Goes on page 1 of your 1040.",
            style = MaterialTheme.typography.bodyMedium)

        current.dependents.forEachIndexed { i, dep ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Dependent ${i + 1}", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = dep.firstName,
                        onValueChange = { v -> updateAt(vm, current.dependents, i, dep.copy(firstName = v)) },
                        label = { Text("First name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = dep.lastName,
                        onValueChange = { v -> updateAt(vm, current.dependents, i, dep.copy(lastName = v)) },
                        label = { Text("Last name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = dep.ssn,
                        onValueChange = { v -> updateAt(vm, current.dependents, i, dep.copy(ssn = formatSsn(v))) },
                        label = { Text("Social security number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = dep.relationship,
                        onValueChange = { v -> updateAt(vm, current.dependents, i, dep.copy(relationship = v)) },
                        label = { Text("Relationship (son, daughter, parent...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = dep.livesWithYou,
                            onCheckedChange = { v -> updateAt(vm, current.dependents, i, dep.copy(livesWithYou = v)) },
                        )
                        Text("Lived with me more than half of 2025")
                    }

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = dep.qualifiesForChildTaxCredit,
                            onCheckedChange = { v -> updateAt(vm, current.dependents, i, dep.copy(qualifiesForChildTaxCredit = v)) },
                        )
                        Column {
                            Text("Qualifies for Child Tax Credit")
                            Text("Under 17, US citizen/resident, has SSN",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    TextButton(onClick = {
                        vm.setDependents(current.dependents.filterIndexed { j, _ -> j != i })
                    }) { Text("Remove") }
                }
            }
        }

        if (current.dependents.size < 4) {
            Button(
                onClick = { vm.setDependents(current.dependents + Dependent()) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Add dependent") }
        } else {
            Text("Maximum 4 dependents. Attach Schedule 8812 for more.",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun updateAt(
    vm: ReturnViewModel,
    list: List<Dependent>,
    index: Int,
    updated: Dependent,
) {
    val next = list.toMutableList()
    next[index] = updated
    vm.setDependents(next)
}

private fun formatSsn(raw: String): String {
    val digits = raw.filter(Char::isDigit).take(9)
    return buildString {
        digits.forEachIndexed { i, c ->
            if (i == 3 || i == 5) append('-')
            append(c)
        }
    }
}
