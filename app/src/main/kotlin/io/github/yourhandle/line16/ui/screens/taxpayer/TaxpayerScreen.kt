package io.github.yourhandle.line16.ui.screens.taxpayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.ui.ReturnViewModel

@Composable
fun TaxpayerScreen(vm: ReturnViewModel) {
    val current by vm.current.collectAsState()
    val tp = current.taxpayer
    val isMarried = current.status == FilingStatus.MFJ

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Your information", style = MaterialTheme.typography.headlineSmall)
        Text("Goes on the top of your 1040. Stays on this device.",
            style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(4.dp))
        Text("Primary taxpayer", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(tp.firstName, { vm.setTaxpayer(tp.copy(firstName = it)) },
            label = { Text("First name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(tp.middleInitial, { vm.setTaxpayer(tp.copy(middleInitial = it.take(1))) },
            label = { Text("Middle initial") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(tp.lastName, { vm.setTaxpayer(tp.copy(lastName = it)) },
            label = { Text("Last name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(tp.ssn, { vm.setTaxpayer(tp.copy(ssn = formatSsn(it))) },
            label = { Text("Social security number") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth())

        if (isMarried) {
            Spacer(Modifier.height(8.dp))
            Text("Spouse", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(tp.spouseFirstName, { vm.setTaxpayer(tp.copy(spouseFirstName = it)) },
                label = { Text("First name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(tp.spouseMiddleInitial, { vm.setTaxpayer(tp.copy(spouseMiddleInitial = it.take(1))) },
                label = { Text("Middle initial") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(tp.spouseLastName, { vm.setTaxpayer(tp.copy(spouseLastName = it)) },
                label = { Text("Last name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(tp.spouseSsn, { vm.setTaxpayer(tp.copy(spouseSsn = formatSsn(it))) },
                label = { Text("Social security number") },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(8.dp))
        Text("Address", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(tp.address, { vm.setTaxpayer(tp.copy(address = it)) },
            label = { Text("Street address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(tp.apt, { vm.setTaxpayer(tp.copy(apt = it)) },
            label = { Text("Apt / unit (optional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(tp.city, { vm.setTaxpayer(tp.copy(city = it)) },
            label = { Text("City") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(tp.state, { vm.setTaxpayer(tp.copy(state = it.uppercase().take(2))) },
            label = { Text("State (2 letters)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(tp.zip, { vm.setTaxpayer(tp.copy(zip = it.filter(Char::isDigit).take(9))) },
            label = { Text("ZIP") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth())
    }
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
