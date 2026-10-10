package com.simoesctt.line16

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simoesctt.line16.ui.ReturnViewModel
import com.simoesctt.line16.ui.nav.Routes
import com.simoesctt.line16.ui.screens.about.AboutScreen
import com.simoesctt.line16.ui.screens.dependents.DependentsScreen
import com.simoesctt.line16.ui.screens.filingstatus.FilingStatusScreen
import com.simoesctt.line16.ui.screens.home.HomeScreen
import com.simoesctt.line16.ui.screens.schedulec.ScheduleCScreen
import com.simoesctt.line16.ui.screens.summary.SummaryScreen
import com.simoesctt.line16.ui.screens.taxpayer.TaxpayerScreen
import com.simoesctt.line16.ui.screens.update.UpdateScreen
import com.simoesctt.line16.ui.screens.w2.W2Screen
import com.simoesctt.line16.ui.theme.Line16Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Line16Theme {
                Line16Root()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Line16Root() {
    val nav = rememberNavController()
    val vm: ReturnViewModel = viewModel()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    val title = when (route) {
        Routes.HOME -> "Line 16"
        Routes.TAXPAYER -> "Your information"
        Routes.DEPENDENTS -> "Dependents"
        Routes.FILING_STATUS -> "Filing status"
        Routes.W2 -> "W-2 income"
        Routes.SCHEDULE_C -> "Schedule C"
        Routes.SUMMARY -> "1040 summary"
        Routes.ABOUT -> "About"
        Routes.UPDATE -> "Form updates"
        else -> "Line 16"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (route != Routes.HOME && route != null) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
            )
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.HOME) {
                HomeScreen(vm) { dest -> nav.navigate(dest) }
            }
            composable(Routes.TAXPAYER) { TaxpayerScreen(vm) }
            composable(Routes.DEPENDENTS) { DependentsScreen(vm) }
            composable(Routes.FILING_STATUS) { FilingStatusScreen(vm) }
            composable(Routes.W2) { W2Screen(vm) }
            composable(Routes.SCHEDULE_C) { ScheduleCScreen(vm) }
            composable(Routes.SUMMARY) { SummaryScreen(vm) }
            composable(Routes.ABOUT) { AboutScreen() }
            composable(Routes.UPDATE) { UpdateScreen() }
        }
    }
}
