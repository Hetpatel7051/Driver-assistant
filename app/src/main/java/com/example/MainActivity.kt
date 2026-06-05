package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.TruckRepository
import com.example.ui.SafarViewModel
import com.example.ui.screens.ActiveNavigationScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.TripHistoryScreen
import com.example.ui.theme.SafarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize local Room database and coordinator repository
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = TruckRepository(db)

        setContent {
            SafarTheme {
                // Instantiating the ViewModel with parameter injection
                val sViewModel: SafarViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            if (modelClass.isAssignableFrom(SafarViewModel::class.java)) {
                                @Suppress("UNCHECKED_CAST")
                                return SafarViewModel(repository) as T
                            }
                            throw IllegalArgumentException("Unknown ViewModel class")
                        }
                    }
                )

                var currentTab by remember { mutableStateOf(TabDestination.NAVIGATION) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            TabDestination.values().forEach { tab ->
                                NavigationBarItem(
                                    selected = currentTab == tab,
                                    onClick = { currentTab = tab },
                                    label = { Text(tab.title) },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == tab) tab.activeIcon else tab.inactiveIcon,
                                            contentDescription = tab.title
                                        )
                                    },
                                    modifier = Modifier.testTag("tab_item_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            TabDestination.NAVIGATION -> {
                                ActiveNavigationScreen(
                                    viewModel = sViewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            TabDestination.HISTORY -> {
                                TripHistoryScreen(
                                    viewModel = sViewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            TabDestination.REMINDERS -> {
                                RemindersScreen(
                                    viewModel = sViewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            TabDestination.PROFILE -> {
                                ProfileScreen(
                                    viewModel = sViewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class TabDestination(
    val title: String,
    val activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    NAVIGATION(
        "Navigation",
        Icons.Default.Send,
        Icons.Default.Send
    ),
    HISTORY(
        "Ledger Log",
        Icons.Default.Home,
        Icons.Default.Home
    ),
    REMINDERS(
        "Reminders",
        Icons.Default.Notifications,
        Icons.Default.Notifications
    ),
    PROFILE(
        "My Profile",
        Icons.Default.Person,
        Icons.Default.Person
    )
}
