package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverProfileDao {
    @Query("SELECT * FROM driver_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<DriverProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: DriverProfile)
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle_details")
    fun getAllVehicles(): Flow<List<VehicleDetails>>

    @Query("SELECT * FROM vehicle_details LIMIT 1")
    fun getActiveVehicle(): Flow<VehicleDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleDetails)

    @Query("DELETE FROM vehicle_details WHERE vehicleNumber = :vehicleNumber")
    suspend fun deleteVehicleByNumber(vehicleNumber: String)
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trip_record ORDER BY timestamp DESC")
    fun getAllTrips(): Flow<List<TripRecord>>

    @Query("SELECT * FROM trip_record WHERE id = :id LIMIT 1")
    suspend fun getTripById(id: Int): TripRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripRecord): Long

    @Update
    suspend fun updateTrip(trip: TripRecord)

    @Delete
    suspend fun deleteTrip(trip: TripRecord)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense_item ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseItem>>

    @Query("SELECT * FROM expense_item WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getExpensesForTrip(tripId: Int): Flow<List<ExpenseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseItem)

    @Delete
    suspend fun deleteExpense(expense: ExpenseItem)
}
