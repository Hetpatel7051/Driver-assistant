package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driver_profile")
data class DriverProfile(
    @PrimaryKey val id: Int = 1, // Let's keep a single active driver profile entry
    val name: String,
    val licenseNumber: String,
    val mobileNumber: String,
    val joiningDate: String = ""
)

@Entity(tableName = "vehicle_details")
data class VehicleDetails(
    @PrimaryKey val vehicleNumber: String, // e.g., "DL-1C-AA-1111"
    val ownerName: String,
    val modelName: String,
    val fuelType: String, // "Diesel", "CNG", "Petrol", "Electric"
    val registrationDate: String,
    val insurancePolicyNumber: String,
    val insuranceExpiryDate: String, // "YYYY-MM-DD"
    val nextServiceDate: String, // "YYYY-MM-DD"
    val nextServiceOdometer: Int = 0,
    val currentOdometer: Int = 0,
    val isVerifiedRto: Boolean = false
)

@Entity(tableName = "trip_record")
data class TripRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startLocation: String,
    val endLocation: String,
    val cargoType: String,
    val cargoWeightKg: Int,
    val deliveryDeadline: String, // e.g., "2026-06-10 18:00"
    val isCompleted: Boolean = false,
    val startOdometer: Int = 0,
    val endOdometer: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val totalDistanceKm: Int = 0
)

@Entity(tableName = "expense_item")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tripId: Int = -1, // Associated with a trip, or -1 for standalone
    val category: String, // "Fuel", "Breakfast", "Food", "Room", "Toll Tax", "Service", "Other"
    val amount: Double,
    val remarks: String,
    val dateStr: String = "", // "YYYY-MM-DD"
    val timestamp: Long = System.currentTimeMillis()
)
