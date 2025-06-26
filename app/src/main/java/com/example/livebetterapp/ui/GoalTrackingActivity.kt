package com.example.livebetterapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import com.example.livebetterapp.repository.AppRepository
import com.example.livebetterapp.viewmodels.ExpenseViewModel
import com.example.livebetterapp.viewmodels.ViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*

class GoalTrackingActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart

    private val viewModel: ExpenseViewModel by viewModels {
        ViewModelFactory(AppRepository(AppDatabase.getInstance(this).appDao()))
    }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_tracking)

        lineChart = findViewById(R.id.lineChart)
        userId = intent.getIntExtra("userId", -1)

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        trackGoal(userId)
    }

    private fun trackGoal(userId: Int) {
        val goalAmount = 500f // your target spending amount

        viewModel.getAllExpenses(userId).observe(this, Observer { expenses ->
            if (expenses.isEmpty()) {
                Toast.makeText(this, "No expenses found", Toast.LENGTH_SHORT).show()
                return@Observer
            }

            // Total spent so far
            val totalSpent = expenses.sumOf { it.amount }

            val actualEntries = listOf(Entry(0f, 0f), Entry(1f, totalSpent.toFloat()))
            val goalEntries = listOf(Entry(0f, goalAmount), Entry(1f, goalAmount))

            val actualDataSet = LineDataSet(actualEntries, "Spent").apply {
                color = ContextCompat.getColor(this@GoalTrackingActivity, R.color.teal_700)
                setCircleColor(color)
                lineWidth = 2f
                circleRadius = 4f
            }

            val goalDataSet = LineDataSet(goalEntries, "Goal").apply {
                color = ContextCompat.getColor(this@GoalTrackingActivity, R.color.purple_500)
                setDrawCircles(false)
                lineWidth = 2f
            }

            val data = LineData(actualDataSet, goalDataSet)
            lineChart.data = data
            lineChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawLabels(false)
            }

            lineChart.axisLeft.textSize = 12f
            lineChart.axisRight.isEnabled = false
            lineChart.description.isEnabled = false
            lineChart.animateY(1000)
            lineChart.invalidate()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
