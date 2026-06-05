package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VehicleDetails
import com.example.ui.SafarViewModel
import com.example.ui.components.GlassmorphicBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.AnimatedSafarLogo
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@Composable
fun RemindersScreen(
    viewModel: SafarViewModel,
    modifier: Modifier = Modifier
) {
    val activeVehicle by viewModel.activeVehicle.collectAsState()

    GlassmorphicBackground(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            Column {
                Text(
                    "Fleet Maintenance & Reminders",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Proactive tracking for dynamic road safety protocols.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (activeVehicle == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Audit Trail Paused",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Link a commercial vehicle to auto-generate reminders on fitness validity, premium expirations, and oil lube schedules.",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        } else {
            val v = activeVehicle!!

            // Calculate countdown values
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = Calendar.getInstance().time

            val insuranceRemainingDays = try {
                val expDate = sdf.parse(v.insuranceExpiryDate)
                if (expDate != null) {
                    val diff = expDate.time - today.time
                    max(0L, diff / (1000 * 60 * 60 * 24)).toInt()
                } else {
                    120
                }
            } catch (e: Exception) {
                120
            }

            val serviceRemainingDays = try {
                val servDate = sdf.parse(v.nextServiceDate)
                if (servDate != null) {
                    val diff = servDate.time - today.time
                    max(0L, diff / (1000 * 60 * 60 * 24)).toInt()
                } else {
                    30
                }
            } catch (e: Exception) {
                30
            }

            val remainingOdo = max(0, v.nextServiceOdometer - v.currentOdometer)

            // Dynamic Level Alerts Warning
            item {
                if (insuranceRemainingDays < 30 || remainingOdo < 2000) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Urgent Road Alerts", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Text("Your fleet details require maintenance attention. Check upcoming schedules below to ensure hassle-free toll transit.", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // 1. INSURANCE ALERT CARD
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.secondary)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Commercial Insurance Status", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Policy: ${v.insurancePolicyNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("EXPIRY DATE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(v.insuranceExpiryDate, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (insuranceRemainingDays < 30) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                )
                            ) {
                                Text(
                                    "$insuranceRemainingDays DAYS LEFT",
                                    color = if (insuranceRemainingDays < 30) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. MAINTENANCE SERVICE CARD
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Build, null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Lube Overhaul & Brake Service", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Scheduled limit: ${v.nextServiceOdometer} KM", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("MILEAGE OVERDUE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$remainingOdo KM REMAINING", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("DUE CALENDAR", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(v.nextServiceDate, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // 3. RTO COMPLIANCE CERTIFICATE CARD
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.tertiary)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("RTO National Clearance", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Registration Date: ${v.registrationDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fitness Validity (Commercial)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("LOCKED ACTIVE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Green Pollutant PUC Slip", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("VALID UNTIL DEC '26", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
        }
    }
}
}
