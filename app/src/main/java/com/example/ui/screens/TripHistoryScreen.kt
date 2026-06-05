package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseItem
import com.example.data.TripRecord
import com.example.ui.SafarViewModel
import com.example.ui.components.GlassmorphicBackground
import com.example.ui.components.GlassCard
import kotlin.math.min

@Composable
fun TripHistoryScreen(
    viewModel: SafarViewModel,
    modifier: Modifier = Modifier
) {
    val trips by viewModel.allTrips.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()

    val completedTrips = trips.filter { it.isCompleted }

    GlassmorphicBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // --- CUMULATIVE CHARTS & SUMMARY ACCENTS ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Expense Allocation Audit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Aggregated view of operating diesel vs driver support costs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                val totalFuelAmount = expenses.filter { it.category == "Fuel" }.sumOf { it.amount }
                val totalPersonalAmount = expenses.filter { it.category != "Fuel" }.sumOf { it.amount }
                val grandTotal = totalFuelAmount + totalPersonalAmount

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Custom Pie / Donut canvas segment
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(80.dp)) {
                            val strokeWidth = 14f
                            val radius = (min(size.width, size.height) / 2f)
                            val centerOffset = Offset(size.width / 2f, size.height / 2f)

                            // Draw empty trace if grandTotal is zero
                            if (grandTotal == 0.0) {
                                drawCircle(
                                    color = Color.LightGray.copy(alpha = 0.3f),
                                    radius = radius,
                                    center = centerOffset,
                                    style = Stroke(strokeWidth)
                                )
                            } else {
                                val fuelAngle = ((totalFuelAmount / grandTotal) * 360f).toFloat()
                                val personalAngle = 360f - fuelAngle

                                // Draw Fuel portion (Amber)
                                drawArc(
                                    color = Color(0xFFF59E0B),
                                    startAngle = -90f,
                                    sweepAngle = fuelAngle,
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                                )

                                // Draw Personal portion (Transit Blue)
                                drawArc(
                                    color = Color(0xFF3B82F6),
                                    startAngle = -90f + fuelAngle,
                                    sweepAngle = personalAngle,
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Total",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "₹${grandTotal.toInt()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Legends listing parameters
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFF59E0B)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fuel: ₹${totalFuelAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(if (grandTotal > 0) "${((totalFuelAmount / grandTotal) * 100).toInt()}%" else "0%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF3B82F6)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Other Costs: ₹${totalPersonalAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(if (grandTotal > 0) "${((totalPersonalAmount / grandTotal) * 100).toInt()}%" else "0%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // --- COMPLETED LISTINGS ---
        Text(
            "Journey History Log",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (completedTrips.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No Completed Traces Found",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Your commercial route deliverables will list here upon finishing voyages in navigation screen.",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(completedTrips) { trip ->
                    val tripExpenses = expenses.filter { it.tripId == trip.id }
                    val fuelCosts = tripExpenses.filter { it.category == "Fuel" }.sumOf { it.amount }
                    val personalCosts = tripExpenses.filter { it.category != "Fuel" }.sumOf { it.amount }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        "Completed",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Voyage #${trip.id}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Badge(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)) {
                                    Text(
                                        "Deliveries Met",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "${trip.startLocation} ➔ ${trip.endLocation}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Load: ${trip.cargoType} (${trip.cargoWeightKg}kg)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Distance: ${trip.totalDistanceKm} KM", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Fuel Cost Audit", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹$fuelCosts", fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B), fontSize = 14.sp)
                                }

                                Column {
                                    Text("Personal costs", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹$personalCosts", fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6), fontSize = 14.sp)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Trip Expenses", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${fuelCosts + personalCosts}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
