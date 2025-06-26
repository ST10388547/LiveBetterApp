package com.example.livebetterapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.livebetterapp.data.models.BudgetGoal
import com.example.livebetterapp.data.models.Category
import com.example.livebetterapp.data.models.Expense
import com.example.livebetterapp.data.models.User

@Database(entities = [User::class, Category::class, Expense::class, BudgetGoal::class], version = 2,exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "live_better_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}
