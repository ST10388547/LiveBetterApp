package com.example.livebetterapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import com.example.livebetterapp.repository.AppRepository
import com.example.livebetterapp.viewmodels.SpendingViewModel
import com.example.livebetterapp.viewmodels.SpendingViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class SpendingGraphActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var viewModel: SpendingViewModel
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spending_graph)

        barChart = findViewById(R.id.barChart)

        // 👇 Get userId from intent
        userId = intent.getIntExtra("userId", -1)

        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val dao = AppDatabase.getInstance(applicationContext).appDao()
        val repository = AppRepository(dao)
        viewModel = ViewModelProvider(
            this,
            SpendingViewModelFactory(repository, userId)
        )[SpendingViewModel::class.java]

        // Observe expenses and categories
        viewModel.expenses.observe(this) { expenses ->
            viewModel.categories.observe(this) { categories ->
                updateChartWithData(expenses, categories)
            }
        }
    }

    private fun updateChartWithData(
        expenses: List<com.example.livebetterapp.data.models.Expense>,
        categories: List<com.example.livebetterapp.data.models.Category>
    ) {
        val categoryMap = categories.associateBy { it.id }
        val spendingByCategory = expenses.groupBy { it.categoryId }
            .mapValues { it.value.sumOf { expense -> expense.amount } }

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        spendingByCategory.entries.forEachIndexed { index, (categoryId, totalAmount) ->
            entries.add(BarEntry(index.toFloat(), totalAmount.toFloat()))
            labels.add(categoryMap[categoryId]?.name ?: "Unknown")
        }

        val dataSet = BarDataSet(entries, "Spending per Category")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        barChart.apply {
            data = BarData(dataSet)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                setDrawLabels(true)
                granularity = 1f
                labelRotationAngle = -45f
            }
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            description.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

