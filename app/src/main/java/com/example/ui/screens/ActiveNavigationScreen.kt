package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseItem
import com.example.data.TripRecord
import com.example.ui.FuelStation
import com.example.ui.SafarViewModel
import com.example.ui.components.GlassmorphicBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.AnimatedSafarLogo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActiveNavigationScreen(
    viewModel: SafarViewModel,
    modifier: Modifier = Modifier
) {
    val activeVehicle by viewModel.activeVehicle.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()

    val activeTrip = allTrips.find { !it.isCompleted }
    val routeSim by viewModel.simulatedRoute.collectAsState()

    // Dispatcher Inputs
    var startPoint by remember { mutableStateOf("") }
    var endPoint by remember { mutableStateOf("") }
    var cargoType by remember { mutableStateOf("Steel Coils") }
    var cargoWeight by remember { mutableStateOf("12000") } // kg
    var deliveryDeadlineHour by remember { mutableStateOf("24") } // hours from now
    var currentOdoInput by remember { mutableStateOf("") }

    // Instant Expense Logger State
    var showExpenseModal by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Toll Tax") }
    var expenseAmount by remember { mutableStateOf("") }
    var expenseRemarks by remember { mutableStateOf("") }
    val categories = listOf("Toll Tax", "Fuel", "Food", "Breakfast", "Room", "Service", "Other")

    // Update form default odometer from active vehicle details
    LaunchedEffect(activeVehicle) {
        if (activeVehicle != null && currentOdoInput.isEmpty()) {
            currentOdoInput = activeVehicle!!.currentOdometer.toString()
        }
    }

    GlassmorphicBackground(
        modifier = modifier.fillMaxSize()
    ) {
        if (activeVehicle == null) {
            // Enforce vehicle binding first
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AnimatedSafarLogo(modifier = Modifier.padding(bottom = 16.dp))
                        
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Vehicle Required",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "You must register or link an active vehicle profile first in the Profile tab to enable cargo logging and matching routing features.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else if (activeTrip == null) {
            // ------------------ NEW TRIP SHIP DESIGN DISPATCH FORM ------------------
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        AnimatedSafarLogo(modifier = Modifier.padding(bottom = 16.dp))
                        
                        Text(
                            "Dispatch Cargo Route",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Configure cargo deadliness and trace compatible station lines.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                "Route Logistics Trace",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = startPoint,
                                onValueChange = { startPoint = it },
                                label = { Text("Starting Location") },
                                leadingIcon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.secondary) },
                                placeholder = { Text("e.g. Mumbai Port Terminus") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_point_input"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = endPoint,
                                onValueChange = { endPoint = it },
                                label = { Text("Destination End Point") },
                                placeholder = { Text("e.g. Indore Industrial Sector 3") },
                                leadingIcon = { Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.primary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("end_point_input"),
                                singleLine = true
                            )
                        }
                    }
                }

                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                "Cargo Load Details",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = cargoType,
                                    onValueChange = { cargoType = it },
                                    label = { Text("Cargo Type") },
                                    modifier = Modifier.weight(1.2f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = cargoWeight,
                                    onValueChange = { cargoWeight = it },
                                    label = { Text("Weight (Kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(0.8f),
                                    singleLine = true
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = deliveryDeadlineHour,
                                    onValueChange = { deliveryDeadlineHour = it },
                                    label = { Text("Deadline (Hrs)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = currentOdoInput,
                                    onValueChange = { currentOdoInput = it },
                                    label = { Text("Start Odo (KM)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Selected vehicle reminder badge
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Settings,
                                        "Suggestion",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "App auto-filters stations matching Active: ${activeVehicle!!.vehicleNumber} (${activeVehicle!!.fuelType} Engine Type)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (startPoint.isNotBlank() && endPoint.isNotBlank() && cargoType.isNotBlank()) {
                                        val startOdo = currentOdoInput.toIntOrNull() ?: activeVehicle!!.currentOdometer
                                        val hrs = deliveryDeadlineHour.toIntOrNull() ?: 24
                                        val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                        val cal = Calendar.getInstance()
                                        cal.add(Calendar.HOUR_OF_DAY, hrs)
                                        val finalDeadlineDate = df.format(cal.time)

                                        viewModel.startTrip(
                                            startPoint,
                                            endPoint,
                                            cargoType,
                                            cargoWeight.toIntOrNull() ?: 5000,
                                            finalDeadlineDate,
                                            startOdo
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("start_trip_button"),
                                shape = RoundedCornerShape(26.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Journey Strategy & Map", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        } else {
            // ------------------ ACTIVE TRIP NAVIGATION SYSTEM OVERLAY ------------------
            val currentTrip = activeTrip
            val activeTripExpenses = expenses.filter { it.tripId == currentTrip.id }
            val fuelType = activeVehicle?.fuelType ?: "Diesel"

            if (routeSim?.tripId != currentTrip.id) {
                viewModel.generateRouteSimulation(currentTrip.id, currentTrip.startLocation, currentTrip.endLocation)
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // High-Density Driver Premium Profile Top Header
                val dProfile by viewModel.driverProfile.collectAsState()
                val aVehicle by viewModel.activeVehicle.collectAsState()

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().testTag("high_density_top_header"),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Initial Avatar Badge
                            val initials = if (!dProfile?.name.isNullOrBlank()) {
                                dProfile!!.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase()
                            } else {
                                "RK"
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD1E4FF))
                            ) {
                                Text(
                                    text = initials,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF001D35)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = dProfile?.name ?: "Rajesh Kumar",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "${aVehicle?.vehicleNumber ?: "MH-12-CQ-4592"} • ${aVehicle?.modelName ?: "TATA 407"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
                    // 1. High-Density Horizontal Alerts Grid
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Alert Card 1: Insurance Expiring
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9DEDC)),
                                border = BorderStroke(1.dp, Color(0xFFB3261E).copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Warning",
                                            tint = Color(0xFFB3261E),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "INSURANCE EXPIRING",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF410E0B)
                                        )
                                    }
                                    Text(
                                        text = "In 4 Days (Mar 28)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF8C1D18)
                                    )
                                }
                            }

                            // Alert Card 2: Service Due
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFD3E3FD)),
                                border = BorderStroke(1.dp, Color(0xFF004A77).copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Build,
                                            contentDescription = "Build",
                                            tint = Color(0xFF004A77),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "SERVICE DUE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF001D35)
                                        )
                                    }
                                    Text(
                                        text = "Next: 45,000 KM",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF004A77)
                                    )
                                }
                            }
                        }
                    }

                    // 2. High-Density Active Trip Overview Card
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth().testTag("active_trip_card_high_density")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFE8DEF8))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE TRIP",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF21005D)
                                        )
                                    }
                                    Text(
                                        text = "ETA: 06:45 PM Today",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF6750A4)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom visual route node connector
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(end = 12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF6750A4))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(18.dp)
                                                .background(Color(0xFF938F99))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .border(1.dp, Color(0xFF6750A4), CircleShape)
                                        )
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(currentTrip.startLocation, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(currentTrip.endLocation, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                        .border(width = 0.5.dp, color = Color(0xFFF3F4F6))
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("CARGO / WEIGHT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF49454F))
                                        Text("${currentTrip.cargoType} / ${currentTrip.cargoWeightKg / 1000.0}T", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("DISTANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF49454F))
                                        Text("${currentTrip.totalDistanceKm} KM Left", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Fuel",
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "2 Diesel Stations on Route (IOCL, HP)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF4B5563)
                                    )
                                }
                        }
                    }

                    // 3. Trip Cost Tracker Card
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth().testTag("trip_cost_tracker_box")
                        ) {
                            Text(
                                text = "Trip Cost Tracker",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF49454F),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // spend sub-card
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFE2E8F0))
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Spend Icon",
                                                    tint = Color(0xFF475569),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Column {
                                                Text("TOTAL SPEND", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                                Text("₹${activeTripExpenses.sumOf { it.amount }.toLong()}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    // mileage sub-card
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFE2E8F0))
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Build,
                                                    contentDescription = "Mileage Icon",
                                                    tint = Color(0xFF475569),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Column {
                                                Text("MILEAGE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                                Text("14.2 km/L", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showExpenseModal = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005FB8)),
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .testTag("cost_tracker_add_expense_btn")
                                    ) {
                                        Text("+ Add Expense", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { showExpenseModal = true },
                                        border = BorderStroke(1.dp, Color(0xFF79747E)),
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Text("View Bills", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                                    }
                                }
                        }
                    }

                    // 4. RTO Integrated Verification Badge Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8DEF8)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* detail integration trigger */ }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified Icon",
                                        tint = Color(0xFF21005D),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text("RTO Integrated Profile", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF21005D))
                                        Text("Vehicle Fitness & RC Verified", fontSize = 11.sp, color = Color(0xFF21005D).copy(alpha = 0.8f))
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Open integration details",
                                    tint = Color(0xFF21005D)
                                )
                            }
                        }
                    }

                    // 5. MOCK CANVAS HIGHWAY GRAPH
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "Live Telematics Line (NH)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Render Route path canvas
                                val traceColor = MaterialTheme.colorScheme.secondary
                                val pointColor = MaterialTheme.colorScheme.primary
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(70.dp)
                                ) {
                                    val h = size.height
                                    val w = size.width

                                    // Draw background path line
                                    drawLine(
                                        color = Color.LightGray.copy(alpha = 0.4f),
                                        start = Offset(20f, h/2),
                                        end = Offset(w - 20f, h/2),
                                        strokeWidth = 12f,
                                        cap = StrokeCap.Round
                                    )

                                    // Draw completed portion path lines
                                    drawLine(
                                        color = traceColor,
                                        start = Offset(20f, h/2),
                                        end = Offset(w * 0.42f, h/2),
                                        strokeWidth = 14f,
                                        cap = StrokeCap.Round
                                    )

                                    // Draw Start point Circle node
                                    drawCircle(
                                        color = traceColor,
                                        radius = 16f,
                                        center = Offset(20f, h/2)
                                    )

                                    // Draw simulated Fuel stations along path
                                    drawCircle(
                                        color = Color.DarkGray,
                                        radius = 12f,
                                        center = Offset(w * 0.25f, h/2 - 10f)
                                    )

                                    drawCircle(
                                        color = Color.DarkGray,
                                        radius = 12f,
                                        center = Offset(w * 0.6f, h/2 + 10f)
                                    )

                                    drawCircle(
                                        color = Color.DarkGray,
                                        radius = 12f,
                                        center = Offset(w * 0.82f, h/2 - 5f)
                                    )

                                    // Draw Truck icon node or glowing active indicator
                                    drawCircle(
                                        color = pointColor,
                                        radius = 20f,
                                        center = Offset(w * 0.42f, h/2)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 8f,
                                        center = Offset(w * 0.42f, h/2)
                                    )

                                    // Draw Destination point node
                                    drawCircle(
                                        color = traceColor,
                                        radius = 18f,
                                        center = Offset(w - 20f, h/2)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("0 KM", style = MaterialTheme.typography.bodySmall)
                                    Text("Simulated Position (42% completed)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("${routeSim?.estimatedDistanceKm ?: 180} KM", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Cargo constraints card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CARGO SPECIFICATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(currentTrip.cargoType, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Weight: ${currentTrip.cargoWeightKg} Kilograms", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("DEADLINE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                        Text(currentTrip.deliveryDeadline.takeLast(5), fontWeight = FontWeight.Black, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }

                    // NAVIGATION COMPATIBLE SUGGESTED FUEL STATIONS SELECTOR
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Compatible Fuel Stations Along Route",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                                Text("$fuelType Matched", modifier = Modifier.padding(horizontal = 4.dp))
                            }
                        }
                    }

                    // Render simulated stations available for this vehicle fuel config
                    val stationList = routeSim?.stationsAvailable ?: emptyList()
                    if (stationList.isEmpty()) {
                        item {
                            Text("No dynamic fuel stations loaded.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    items(stationList) { station ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (station.isAvailable) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (station.isAvailable) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            ListItem(
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                leadingContent = {
                                    Surface(
                                        color = if (station.isAvailable) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Info,
                                                null,
                                                tint = if (station.isAvailable) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                },
                                headlineContent = {
                                    Text(
                                        station.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                },
                                supportingContent = {
                                    Column {
                                        Text(station.location, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "Match Price: ₹${station.priceUnit} per units",
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                trailingContent = {
                                    Badge(
                                        containerColor = if (station.isAvailable) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                                    ) {
                                        Text(
                                            station.availabilityStatus,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // CURRENT ACTIVE TRIP EXPENSES MINI LEDGER
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Trip Expenditures Logged",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "Total: ₹${activeTripExpenses.sumOf { it.amount }}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    if (activeTripExpenses.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No expenses logged on this trip yet. Tap 'Log Expense' below immediately mapping Toll Tax, Food, or Diesel.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(activeTripExpenses) { exp ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Badge(
                                                containerColor = when (exp.category) {
                                                    "Fuel" -> MaterialTheme.colorScheme.primary
                                                    "Toll Tax" -> MaterialTheme.colorScheme.secondary
                                                    "Food", "Breakfast" -> MaterialTheme.colorScheme.tertiary
                                                    else -> MaterialTheme.colorScheme.outline
                                                }
                                            ) {
                                                Text(
                                                    exp.category,
                                                    modifier = Modifier.padding(horizontal = 4.dp),
                                                    color = Color.White,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                exp.remarks,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(exp.dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "₹${exp.amount}",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(onClick = { viewModel.removeExpense(exp) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // FLOATING ACTION FOOTER BUTTONS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showExpenseModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("log_expense_drawer_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Expense", fontWeight = FontWeight.Bold)
                    }

                    // Complete route action
                    var showCompleteDialog by remember { mutableStateOf(false) }
                    var finalOdoValue by remember { mutableStateOf((currentTrip.startOdometer + 130).toString()) }

                    Button(
                        onClick = { showCompleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("complete_trip_flow_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Finish Route", fontWeight = FontWeight.Bold)
                    }

                    if (showCompleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showCompleteDialog = false },
                            title = { Text("Complete Delivery Route") },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Log the final vehicle odometer reading relative to the start point (${currentTrip.startOdometer} KM) to lock the distance and release cargo limits.")
                                    OutlinedTextField(
                                        value = finalOdoValue,
                                        onValueChange = { finalOdoValue = it },
                                        label = { Text("Final Odo Reading (KM)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val finalVal = finalOdoValue.toIntOrNull() ?: (currentTrip.startOdometer + 130)
                                        viewModel.completeTrip(currentTrip.id, finalVal)
                                        showCompleteDialog = false
                                    }
                                ) {
                                    Text("LOCK DELIVERED", fontWeight = FontWeight.Black)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showCompleteDialog = false }) {
                                    Text("CANCEL")
                                }
                            }
                        )
                    }
                }
            }

            // MODAL EXPENSE LOGGER DRAWER / SHEET DIALOG
            if (showExpenseModal) {
                AlertDialog(
                    onDismissRequest = { showExpenseModal = false },
                    title = { Text("Add Personal or Trip Expenditure", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Category:")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Just simple categorization spinner elements mapping Categories
                                val chunkedCats = categories.chunked(4)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    chunkedCats.forEach { row ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            row.forEach { cat ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                            if (selectedCategory == cat) MaterialTheme.colorScheme.primary else Color.Transparent
                                                        )
                                                        .clickable { selectedCategory = cat }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        cat,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (selectedCategory == cat) Color.Black else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = expenseAmount,
                                onValueChange = { expenseAmount = it },
                                label = { Text("Amount (₹ INr)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("expense_amount_input"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = expenseRemarks,
                                onValueChange = { expenseRemarks = it },
                                label = { Text("Remarks (e.g. Punjabi Dhaba Food)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("expense_remarks_input"),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val amt = expenseAmount.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    val dfObj = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    viewModel.addExpense(
                                        tripId = currentTrip.id,
                                        category = selectedCategory,
                                        amount = amt,
                                        remarks = expenseRemarks.ifBlank { selectedCategory },
                                        dateStr = dfObj.format(Date())
                                    )
                                    // Reset fields
                                    expenseAmount = ""
                                    expenseRemarks = ""
                                    showExpenseModal = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Save Ledger", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExpenseModal = false }) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    }
}
