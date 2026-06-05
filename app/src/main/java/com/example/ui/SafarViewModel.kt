package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SafarViewModel(private val repository: TruckRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            // Seed driver profile if none exists
            val currentProfile = repository.driverProfile.firstOrNull()
            if (currentProfile == null) {
                repository.saveProfile(DriverProfile(
                    name = "Rajesh Kumar",
                    licenseNumber = "MH-12-CQ-4592-DL",
                    mobileNumber = "+91 98765 43210",
                    joiningDate = "2024-03-12"
                ))
            }

            // Seed active TATA 407 vehicle details
            val vehicles = repository.allVehicles.firstOrNull() ?: emptyList()
            if (vehicles.isEmpty()) {
                repository.saveVehicle(VehicleDetails(
                    vehicleNumber = "MH-12-CQ-4592",
                    ownerName = "Rajesh Kumar",
                    modelName = "TATA 407",
                    fuelType = "Diesel",
                    registrationDate = "2024-03-15",
                    insurancePolicyNumber = "POL-INS-45921A",
                    insuranceExpiryDate = "2026-06-28", // Expiring soon
                    nextServiceDate = "2026-07-20",
                    nextServiceOdometer = 45000,
                    currentOdometer = 32800,
                    isVerifiedRto = true
                ))
            }

            // Seed active JNPT-Chakan trip logs and expense details if empty
            val trips = repository.allTrips.firstOrNull() ?: emptyList()
            if (trips.isEmpty()) {
                val newTrip = TripRecord(
                    startLocation = "Mumbai (JNPT Port)",
                    endLocation = "Pune (Chakan MIDC)",
                    cargoType = "Machine Parts",
                    cargoWeightKg = 2800, // 2.8 Ton
                    deliveryDeadline = "2026-06-05 18:45",
                    startOdometer = 32800,
                    isCompleted = false,
                    totalDistanceKm = 148
                )
                val tripId = repository.createTrip(newTrip)

                // Add sample expenses (₹3,250 + ₹1,000 = ₹4,250 total spend)
                repository.addExpense(ExpenseItem(
                    tripId = tripId,
                    category = "Fuel",
                    amount = 3250.0,
                    remarks = "Nayara Premium Diesel Refill",
                    dateStr = "2026-06-05"
                ))
                repository.addExpense(ExpenseItem(
                    tripId = tripId,
                    category = "Toll Tax",
                    amount = 1000.0,
                    remarks = "Kharghar Expressway Tollway Plaza",
                    dateStr = "2026-06-05"
                ))
            }
        }
    }

    // Driver Profile State
    val driverProfile: StateFlow<DriverProfile?> = repository.driverProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Vehicle States
    val activeVehicle: StateFlow<VehicleDetails?> = repository.activeVehicle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allVehicles: StateFlow<List<VehicleDetails>> = repository.allVehicles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Trip States
    val allTrips: StateFlow<List<TripRecord>> = repository.allTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expense States
    val allExpenses: StateFlow<List<ExpenseItem>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Navigation / Routing simulator states
    private val _rtoSearchState = MutableStateFlow<RtoDetailsResult?>(null)
    val rtoSearchState: StateFlow<RtoDetailsResult?> = _rtoSearchState.asStateFlow()

    // Route Simulator state
    private val _simulatedRoute = MutableStateFlow<RouteSimulation?>(null)
    val simulatedRoute: StateFlow<RouteSimulation?> = _simulatedRoute.asStateFlow()

    fun searchRtoDetails(vehicleNumber: String) {
        viewModelScope.launch {
            repository.fetchRtoDetails(vehicleNumber).collect { result ->
                _rtoSearchState.value = result
            }
        }
    }

    fun clearRtoSearch() {
        _rtoSearchState.value = null
    }

    fun saveDriverProfile(name: String, licenseNum: String, mobileNum: String, joinDate: String) {
        viewModelScope.launch {
            val profile = DriverProfile(name = name, licenseNumber = licenseNum, mobileNumber = mobileNum, joiningDate = joinDate)
            repository.saveProfile(profile)
        }
    }

    fun saveVehicleDetails(vehicle: VehicleDetails) {
        viewModelScope.launch {
            repository.saveVehicle(vehicle)
        }
    }

    fun deleteVehicle(vehicleNumber: String) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicleNumber)
        }
    }

    fun startTrip(
        startLoc: String,
        endLoc: String,
        cargo: String,
        weight: Int,
        deadline: String,
        startOdo: Int
    ) {
        viewModelScope.launch {
            val trip = TripRecord(
                startLocation = startLoc,
                endLocation = endLoc,
                cargoType = cargo,
                cargoWeightKg = weight,
                deliveryDeadline = deadline,
                startOdometer = startOdo,
                isCompleted = false,
                totalDistanceKm = 120 + (weight % 180) // Deterministic mock distance based on logistics
            )
            val tripId = repository.createTrip(trip)
            
            // Build mapped station details matching the vehicle's fuel type configuration
            generateRouteSimulation(tripId, startLoc, endLoc)
        }
    }

    fun updateOdometerOnTrip(tripId: Int, currentOdo: Int) {
        viewModelScope.launch {
            val existing = allTrips.value.find { it.id == tripId } ?: return@launch
            repository.updateTrip(existing.copy(endOdometer = currentOdo))
        }
    }

    /**
     * Completes an active trip
     */
    fun completeTrip(tripId: Int, finalOdo: Int) {
        viewModelScope.launch {
            val existing = allTrips.value.find { it.id == tripId } ?: return@launch
            val updated = existing.copy(
                isCompleted = true,
                endOdometer = finalOdo,
                totalDistanceKm = if (finalOdo > existing.startOdometer) finalOdo - existing.startOdometer else existing.totalDistanceKm
            )
            repository.updateTrip(updated)
            
            // If we have completed, clear the current live navigation simulator
            if (_simulatedRoute.value?.tripId == tripId) {
                _simulatedRoute.value = null
            }

            // Also update vehicle current cumulative odometer to match
            activeVehicle.value?.let { v ->
                if (finalOdo > v.currentOdometer) {
                    repository.saveVehicle(v.copy(currentOdometer = finalOdo))
                }
            }
        }
    }

    fun cancelActiveTrip(trip: TripRecord) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
            _simulatedRoute.value = null
        }
    }

    fun addExpense(
        tripId: Int,
        category: String,
        amount: Double,
        remarks: String,
        dateStr: String
    ) {
        viewModelScope.launch {
            val expense = ExpenseItem(
                tripId = tripId,
                category = category,
                amount = amount,
                remarks = remarks,
                dateStr = dateStr
            )
            repository.addExpense(expense)
        }
    }

    fun removeExpense(expense: ExpenseItem) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun generateRouteSimulation(tripId: Int, start: String, end: String) {
        val fType = activeVehicle.value?.fuelType ?: "Diesel"
        
        // Define stations along the simulated route matching specific fuel type
        val stations = when (fType) {
            "CNG" -> listOf(
                FuelStation("Mahanagar CNG Filling Hub", "Hassan Road, 18km out", "CNG", 92.5, "Active", true),
                FuelStation("Gujarat Gas Green CNG", "By-pass Junction, 45km out", "CNG", 90.0, "Busy Limit", true),
                FuelStation("Indraprastha Green Express", "National Highway 48, 85km out", "CNG", 93.2, "Out of Stock", false)
            )
            "Electric" -> listOf(
                FuelStation("Tata Power EZ Charge (Hyper)", "Highway Plaza, 22km out", "Electric", 15.0, "2 Ports Free", true),
                FuelStation("Jio-bp pulse EV station", "Milestone 60 Terminal", "Electric", 18.0, "Fully Occupied", false),
                FuelStation("National Grid Rapid Charger", "Service Lane Exit, 98km out", "Electric", 12.0, "1 Port Free", true)
            )
            "Petrol" -> listOf(
                FuelStation("IndianOil Swarna Outpost", "Main Bypass, 15km out", "Petrol", 104.2, "Active", true),
                FuelStation("Shell Power Fuel Stop", "Bridge Exit, 55km out", "Petrol", 109.8, "Active", true),
                FuelStation("Bharat Petroleum Premium", "Terminal Area, 105km out", "Petrol", 103.5, "Active", true)
            )
            else -> listOf( // Diesel (Default)
                FuelStation("IndianOil Highway Swagat", "Service Exit 10, 14km out", "Diesel", 89.4, "High Flow Enabled", true),
                FuelStation("Nayara Smart Diesel Station", "Ring Circle, 38km out", "Diesel", 88.9, "Active", true),
                FuelStation("Bharat Petroleum Ghar", "Express Bypass, 72km out", "Diesel", 90.1, "Under Maintenance", false),
                FuelStation("HP High-Speed Diesel Hub", "Industrial Belt Road, 110km out", "Diesel", 89.0, "Active", true)
            )
        }

        _simulatedRoute.value = RouteSimulation(
            tripId = tripId,
            startPoint = start,
            endPoint = end,
            fuelTypeRequired = fType,
            stationsAvailable = stations,
            estimatedDistanceKm = 120 + (tripId % 50),
            estimatedDurationHours = 3.5 + (tripId % 3) * 0.5
        )
    }

    fun endSimulation() {
        _simulatedRoute.value = null
    }
}

// Data models representing dynamic route configurations
data class RouteSimulation(
    val tripId: Int,
    val startPoint: String,
    val endPoint: String,
    val fuelTypeRequired: String,
    val stationsAvailable: List<FuelStation>,
    val estimatedDistanceKm: Int,
    val estimatedDurationHours: Double
)

data class FuelStation(
    val name: String,
    val location: String,
    val fuelType: String,
    val priceUnit: Double, // Price per Litre / kg / kWh
    val availabilityStatus: String, // "Active", "Out of Stock", "Ports occupied", etc.
    val isAvailable: Boolean
)
