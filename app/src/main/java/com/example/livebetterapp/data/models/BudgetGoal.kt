package com.example.livebetterapp.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "budget_goals")
data class BudgetGoal(
    @PrimaryKey val userId: Int,
    val minGoal: Double,
    val maxGoal: Double
)
