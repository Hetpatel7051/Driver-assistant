package com.example.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TruckRepository(private val db: AppDatabase) {
    val driverProfile: Flow<DriverProfile?> = db.driverProfileDao().getProfile()
    val allVehicles: Flow<List<VehicleDetails>> = db.vehicleDao().getAllVehicles()
    val activeVehicle: Flow<VehicleDetails?> = db.vehicleDao().getActiveVehicle()
    val allTrips: Flow<List<TripRecord>> = db.tripDao().getAllTrips()
    val allExpenses: Flow<List<ExpenseItem>> = db.expenseDao().getAllExpenses()

    fun getExpensesForTrip(tripId: Int): Flow<List<ExpenseItem>> {
        return db.expenseDao().getExpensesForTrip(tripId)
    }

    suspend fun saveProfile(profile: DriverProfile) {
        db.driverProfileDao().insertProfile(profile)
    }

    suspend fun saveVehicle(vehicle: VehicleDetails) {
        db.vehicleDao().insertVehicle(vehicle)
    }

    suspend fun deleteVehicle(vehicleNumber: String) {
        db.vehicleDao().deleteVehicleByNumber(vehicleNumber)
    }

    suspend fun createTrip(trip: TripRecord): Int {
        return db.tripDao().insertTrip(trip).toInt()
    }

    suspend fun updateTrip(trip: TripRecord) {
        db.tripDao().updateTrip(trip)
    }

    suspend fun deleteTrip(trip: TripRecord) {
        db.tripDao().deleteTrip(trip)
    }

    suspend fun addExpense(expense: ExpenseItem) {
        db.expenseDao().insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseItem) {
        db.expenseDao().deleteExpense(expense)
    }

    /**
     * Simulated RTO fetch returning structured mock data for commercial vehicles
     */
    fun fetchRtoDetails(vehicleNumber: String): Flow<RtoDetailsResult> = flow {
        emit(RtoDetailsResult.Loading)
        delay(1500) // Simulate network delay

        val cleanNum = vehicleNumber.uppercase().trim().replace("\\s+".toRegex(), "")
        if (cleanNum.length < 5) {
            emit(RtoDetailsResult.Error("Invalid registration number format. Please enter a valid vehicle number (e.g., MH-12-PQ-1234)."))
            return@flow
        }

        // Generate deterministic details based on the vehicle number
        val model = when {
            cleanNum.contains("TATA") || cleanNum.last() in 'A'..'G' -> "Tata Ace Gold (Chota Hathi)"
            cleanNum.contains("MAH") || cleanNum.last() in 'H'..'N' -> "Mahindra Bolero Maxx Pik-Up"
            cleanNum.contains("ASH") || cleanNum.last() in 'O'..'T' -> "Ashok Leyland Dost LiTE"
            else -> "BharatBenz 1917R Tipper"
        }

        val fuelType = when {
            cleanNum.contains("C") || cleanNum.contains("CNG") || cleanNum.last() in 'E'..'J' -> "CNG"
            cleanNum.contains("E") || cleanNum.contains("EV") || cleanNum.last() in 'K'..'N' -> "Electric"
            cleanNum.last() in 'O'..'R' -> "Petrol"
            else -> "Diesel"
        }

        val owner = "Baldev Singh (S/O Gurcharan Singh)"
        val regDate = "2024-03-15"
        val policyNum = "POL-INS-${cleanNum.hashCode().coerceAtLeast(0) % 90000 + 10000}"
        
        // Provide future expiration dates relative to current 2026-06 date
        val insuranceExpiry = "2027-03-14"
        val upcomingService = "2026-07-20"
        val nextServiceOdometer = 45000
        val currentOmit = 38450

        val details = VehicleDetails(
            vehicleNumber = vehicleNumber.uppercase(),
            ownerName = owner,
            modelName = model,
            fuelType = fuelType,
            registrationDate = regDate,
            insurancePolicyNumber = policyNum,
            insuranceExpiryDate = insuranceExpiry,
            nextServiceDate = upcomingService,
            nextServiceOdometer = nextServiceOdometer,
            currentOdometer = currentOmit,
            isVerifiedRto = true
        )

        emit(RtoDetailsResult.Success(details))
    }
}

sealed interface RtoDetailsResult {
    object Loading : RtoDetailsResult
    data class Success(val details: VehicleDetails) : RtoDetailsResult
    data class Error(val message: String) : RtoDetailsResult
}
