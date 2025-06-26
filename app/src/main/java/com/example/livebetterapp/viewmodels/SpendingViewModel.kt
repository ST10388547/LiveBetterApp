package com.example.livebetterapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.livebetterapp.data.models.Category
import com.example.livebetterapp.data.models.Expense
import com.example.livebetterapp.repository.AppRepository

class SpendingViewModel(repository: AppRepository, userId: Int) : ViewModel() {
    val expenses: LiveData<List<Expense>> = repository.getAllExpensesForUser(userId)
    val categories: LiveData<List<Category>> = repository.getAllCategories()
}