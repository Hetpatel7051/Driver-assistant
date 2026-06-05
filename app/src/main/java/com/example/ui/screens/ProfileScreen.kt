package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DriverProfile
import com.example.data.RtoDetailsResult
import com.example.data.VehicleDetails
import com.example.ui.SafarViewModel
import com.example.ui.components.GlassmorphicBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.AnimatedSafarLogo

@Composable
fun ProfileScreen(
    viewModel: SafarViewModel,
    modifier: Modifier = Modifier
) {
    val driver by viewModel.driverProfile.collectAsState()
    val activeVehicle by viewModel.activeVehicle.collectAsState()
    val rtoResult by viewModel.rtoSearchState.collectAsState()

    var isEditDriverMode by remember { mutableStateOf(driver == null) }
    var driverName by remember(driver) { mutableStateOf(driver?.name ?: "") }
    var driverLicense by remember(driver) { mutableStateOf(driver?.licenseNumber ?: "") }
    var driverPhone by remember(driver) { mutableStateOf(driver?.mobileNumber ?: "") }

    var rtoPlateInput by remember { mutableStateOf("") }
    
    // Manual Vehicle edit inputs
    var isManualVehicleMode by remember { mutableStateOf(false) }
    var vehNumber by remember { mutableStateOf("") }
    var vehOwnerName by remember { mutableStateOf("") }
    var vehModelName by remember { mutableStateOf("") }
    var vehFuelType by remember { mutableStateOf("Diesel") }
    val fuelTypes = listOf("Diesel", "CNG", "Petrol", "Electric")
    var vehInsurancePolicy by remember { mutableStateOf("") }
    var vehInsuranceExpiry by remember { mutableStateOf("2027-04-12") }
    var vehNextServiceDate by remember { mutableStateOf("2026-08-15") }
    var vehNextServiceOdo by remember { mutableStateOf("45000") }
    var vehCurrentOdo by remember { mutableStateOf("38000") }

    val keyboardController = LocalSoftwareKeyboardController.current

    GlassmorphicBackground(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
        // --- DRIVER CARD HEADER ---
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Driver Profile",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = driver?.name ?: "Set Up Profile",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditDriverMode) {
                        OutlinedTextField(
                            value = driverName,
                            onValueChange = { driverName = it },
                            label = { Text("Driver Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = driverLicense,
                            onValueChange = { driverLicense = it.uppercase() },
                            label = { Text("Commercial Driving License (DL)") },
                            leadingIcon = { Icon(Icons.Default.Info, null) },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = driverPhone,
                            onValueChange = { driverPhone = it },
                            label = { Text("Mobile Number (linked to RTO)") },
                            leadingIcon = { Icon(Icons.Default.Phone, null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (driverName.isNotBlank() && driverLicense.isNotBlank()) {
                                    viewModel.saveDriverProfile(
                                        driverName,
                                        driverLicense,
                                        driverPhone,
                                        "2026-06-05"
                                    )
                                    isEditDriverMode = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_driver_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Driver Details", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Display view-only mode
                        driver?.let { d ->
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("DL License Number:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                Text(d.licenseNumber, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Registered Contact:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                Text(d.mobileNumber, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { isEditDriverMode = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Edit Driver Profile")
                            }
                        } ?: Text(
                            "Kindly fill driver profile to synchronize logistics parameters.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
            }
        }

        // --- RTO AUTO INTEGRATION / VEHICLE MANAGEMENT ---
        item {
            Text(
                text = "Vehicle Management",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // RTO Instant Verification Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                "RTO Digital Integration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Auto-import vehicle details & insurance alerts",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Enter Commercial Registration Mark / Plate Number:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = rtoPlateInput,
                            onValueChange = { rtoPlateInput = it.uppercase() },
                            placeholder = { Text("e.g., MH-12-PQ-1234") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("rto_plate_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                        )

                        Button(
                            onClick = {
                                if (rtoPlateInput.isNotBlank()) {
                                    keyboardController?.hide()
                                    viewModel.searchRtoDetails(rtoPlateInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("rto_verify_button")
                        ) {
                            Text("Verify RTO", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Render RTO query outputs
                    AnimatedVisibility(
                        visible = rtoResult != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            when (val result = rtoResult) {
                                RtoDetailsResult.Loading -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            "Contacting Parivahan RTO gateway...",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                is RtoDetailsResult.Success -> {
                                    val rtoVeh = result.details
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                                            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    "Verified",
                                                    tint = MaterialTheme.colorScheme.tertiary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    "RTO Records Found & Verified",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.tertiary
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Owner: ${rtoVeh.ownerName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Model: ${rtoVeh.modelName}", fontSize = 12.sp)
                                            Text("Fuel Type: ${rtoVeh.fuelType}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Policy ID: ${rtoVeh.insurancePolicyNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("Insurance Expiry: ${rtoVeh.insuranceExpiryDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)

                                            Spacer(modifier = Modifier.height(12.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.saveVehicleDetails(rtoVeh)
                                                    viewModel.clearRtoSearch()
                                                    rtoPlateInput = ""
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                            ) {
                                                Text("Link Verified Vehicle to App", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                is RtoDetailsResult.Error -> {
                                    Text(
                                        result.message,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                null -> {}
                            }
                        }
                    }
                }
            }
        }

        // Active Vehicle Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Settings,
                                "Active Truck",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Linked Vehicle",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        if (activeVehicle != null) {
                            IconButton(onClick = { viewModel.deleteVehicle(activeVehicle!!.vehicleNumber) }) {
                                Icon(Icons.Default.Delete, "Delete Vehicle", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    activeVehicle?.let { v ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        v.vehicleNumber,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Badge(
                                        containerColor = if (v.isVerifiedRto) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                                    ) {
                                        Text(
                                            if (v.isVerifiedRto) "RTO Verified" else "Manual Entry",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Model: ${v.modelName}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Owner: ${v.ownerName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Fuel Type", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Settings,
                                                null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(v.fuelType, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Odometer Meter", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${v.currentOdometer} KM", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render Quick service limits
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Insurance Policy", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(v.insurancePolicyNumber.take(12), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text("Until: ${v.insuranceExpiryDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Next Scheduled Service", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${v.nextServiceOdometer} KM Limit", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text("Due: ${v.nextServiceDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                    } ?: Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Truck Linked Yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { isManualVehicleMode = !isManualVehicleMode },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                        ) {
                            Text(if (isManualVehicleMode) "Cancel Manual Profile" else "Create Manual Vehicle Detail")
                        }
                    }

                    // Manual Setup expansion block
                    AnimatedVisibility(
                        visible = isManualVehicleMode && activeVehicle == null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = vehNumber,
                                onValueChange = { vehNumber = it.uppercase() },
                                label = { Text("Vehicle Number Mark") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = vehOwnerName,
                                onValueChange = { vehOwnerName = it },
                                label = { Text("Registered Owner Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = vehModelName,
                                onValueChange = { vehModelName = it },
                                label = { Text("Vehicle Specification / Model") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text("Select Fuel Strategy Strategy:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                fuelTypes.forEach { type ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (vehFuelType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { vehFuelType = type }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            type,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (vehFuelType == type) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = vehInsurancePolicy,
                                onValueChange = { vehInsurancePolicy = it },
                                label = { Text("Insurance Policy Scheme") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = vehInsuranceExpiry,
                                    onValueChange = { vehInsuranceExpiry = it },
                                    label = { Text("Insurance End") },
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = vehCurrentOdo,
                                    onValueChange = { vehCurrentOdo = it },
                                    label = { Text("Current Odo (KM)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Button(
                                onClick = {
                                    if (vehNumber.isNotBlank() && vehOwnerName.isNotBlank() && vehModelName.isNotBlank()) {
                                        val newVeh = VehicleDetails(
                                            vehicleNumber = vehNumber,
                                            ownerName = vehOwnerName,
                                            modelName = vehModelName,
                                            fuelType = vehFuelType,
                                            registrationDate = "2024-05-12",
                                            insurancePolicyNumber = vehInsurancePolicy.ifBlank { "POL-DEFAULT-99" },
                                            insuranceExpiryDate = vehInsuranceExpiry,
                                            nextServiceDate = vehNextServiceDate,
                                            nextServiceOdometer = vehNextServiceOdo.toIntOrNull() ?: 45000,
                                            currentOdometer = vehCurrentOdo.toIntOrNull() ?: 38000,
                                            isVerifiedRto = false
                                        )
                                        viewModel.saveVehicleDetails(newVeh)
                                        isManualVehicleMode = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Bind Manual Details", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
}
