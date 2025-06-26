package com.example.livebetterapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livebetterapp.data.models.*
import com.example.livebetterapp.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseViewModel(private val repository: AppRepository) : ViewModel() {

    fun insertExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertExpense(expense)
        }
    }

    fun getExpenses(userId: Int, startDate: String, endDate: String): LiveData<List<Expense>> {
        return repository.getExpensesInRange(userId, startDate, endDate)
    }

    fun getAllExpenses(userId: Int): LiveData<List<Expense>> {
        return repository.getAllExpensesForUser(userId)
    }

    fun insertOrUpdateBudgetGoal(goal: BudgetGoal) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertOrUpdateBudgetGoal(goal)
        }
    }

    fun getBudgetGoal(userId: Int): LiveData<BudgetGoal?> {
        return repository.getBudgetGoal(userId)
    }


    suspend fun insertCategory(category: Category): Long {
        return withContext(Dispatchers.IO) {
            repository.insertCategory(category)
        }
    }

    fun getAllCategories(): LiveData<List<Category>> {
        return repository.getAllCategories()
    }
}

