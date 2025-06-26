package com.example.livebetterapp.repository

import androidx.lifecycle.LiveData
import com.example.livebetterapp.data.db.AppDao
import com.example.livebetterapp.data.models.BudgetGoal
import com.example.livebetterapp.data.models.Category
import com.example.livebetterapp.data.models.Expense
import com.example.livebetterapp.data.models.User

class AppRepository(private val dao: AppDao) {

    // User
    suspend fun registerUser(user: User) = dao.registerUser(user)
    suspend fun loginUser(username: String, password: String) = dao.loginUser(username, password)

    // Category
    suspend fun insertCategory(category: Category) = dao.insertCategory(category)
    fun getAllCategories(): LiveData<List<Category>> = dao.getAllCategories()

    // Expenses
    suspend fun insertExpense(expense: Expense) = dao.insertExpense(expense)
    fun getExpensesInRange(userId: Int, startDate: String, endDate: String): LiveData<List<Expense>> =
        dao.getExpensesInRange(userId, startDate, endDate)

    fun getAllExpensesForUser(userId: Int): LiveData<List<Expense>> {
        return dao.getAllExpensesForUser(userId)
    }

    // Budget Goal
    suspend fun insertOrUpdateBudgetGoal(goal: BudgetGoal) = dao.insertOrUpdateBudgetGoal(goal)
    fun getBudgetGoal(userId: Int): LiveData<BudgetGoal?> = dao.getBudgetGoal(userId)
}
