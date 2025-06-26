package com.example.livebetterapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.livebetterapp.data.models.BudgetGoal
import com.example.livebetterapp.data.models.Category
import com.example.livebetterapp.data.models.Expense
import com.example.livebetterapp.data.models.User

@Dao
interface AppDao {

    // -------------------- Users --------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun loginUser(username: String, password: String): User?


    // -------------------- Categories --------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>


    // -------------------- Expenses --------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Query("SELECT * FROM expenses WHERE userId = :userId")
    fun getAllExpensesForUser(userId: Int): LiveData<List<Expense>>



    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getExpensesInRange(userId: Int, startDate: String, endDate: String): LiveData<List<Expense>>

    @Query("SELECT DISTINCT date FROM expenses WHERE date >= :startDate")
    suspend fun getLoggedDaysSince(startDate: String): List<String>


    // -------------------- Budget Goals --------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudgetGoal(goal: BudgetGoal)

    @Query("SELECT * FROM budget_goals WHERE userId = :userId")
    fun getBudgetGoal(userId: Int): LiveData<BudgetGoal?>
}
