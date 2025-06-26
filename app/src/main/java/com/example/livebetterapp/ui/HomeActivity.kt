package com.example.livebetterapp.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import com.example.livebetterapp.repository.AppRepository
import com.example.livebetterapp.viewmodels.ExpenseViewModel
import com.example.livebetterapp.viewmodels.ViewModelFactory
import java.util.Calendar
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {

    private val viewModel: ExpenseViewModel by viewModels {
        ViewModelFactory(AppRepository(AppDatabase.getInstance(this).appDao()))
    }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        userId = intent.getIntExtra("userId", -1)
        val username = intent.getStringExtra("username") ?: "User"


        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvBudget = findViewById<TextView>(R.id.tvBudgetGoal)
        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)
        val btnViewExpenses = findViewById<Button>(R.id.btnViewExpenses)
        val btnSetBudget = findViewById<Button>(R.id.btnSetBudget)
        val btnViewGraph = findViewById<Button>(R.id.btnViewGraph)
        val btnCategorySummary = findViewById<Button>(R.id.btnCategorySummary)
        val btnGamification = findViewById<Button>(R.id.btnGamification)
        val btnGoalTracking = findViewById<Button>(R.id.btnGoalTracking)


        tvWelcome.text = getString(R.string.welcome_message, username)


        viewModel.getBudgetGoal(userId).observe(this, Observer { goal ->
            tvBudget.text = if (goal != null) {
                getString(R.string.budget_goal_format, goal.minGoal, goal.maxGoal)
            } else {
                getString(R.string.no_budget_set)
            }
        })


        btnAddExpense.setOnClickListener {
            startActivityWithUserId(AddExpenseActivity::class.java)
        }

        btnViewExpenses.setOnClickListener {
            startActivityWithUserId(ViewExpensesActivity::class.java)
        }

        btnSetBudget.setOnClickListener {
            startActivityWithUserId(SetBudgetActivity::class.java)
        }

        btnViewGraph.setOnClickListener {
            startActivityWithUserId(SpendingGraphActivity::class.java)
        }

        btnCategorySummary.setOnClickListener{
            startActivityWithUserId(CategorySummaryActivity::class.java)
        }

        btnGamification.setOnClickListener {
            startActivityWithUserId(GamificationActivity::class.java)
        }

        btnGoalTracking.setOnClickListener {
            startActivityWithUserId(GoalTrackingActivity::class.java)
        }


        scheduleDailyReminder()
    }

    private fun startActivityWithUserId(activityClass: Class<*>) {
        val intent = Intent(this, activityClass).apply {
            putExtra("userId", userId)
        }
        startActivity(intent)
    }

    private fun scheduleDailyReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        NotificationHelper.createNotificationChannel(this)
    }
}
