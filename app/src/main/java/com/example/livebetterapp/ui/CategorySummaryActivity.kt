package com.example.livebetterapp.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import com.example.livebetterapp.data.models.Category
import com.example.livebetterapp.data.models.Expense
import com.example.livebetterapp.repository.AppRepository
import com.example.livebetterapp.viewmodels.ExpenseViewModel
import com.example.livebetterapp.viewmodels.ViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.*
import kotlin.collections.HashMap

class CategorySummaryActivity : AppCompatActivity() {

    private lateinit var etStart: EditText
    private lateinit var etEnd: EditText
    private lateinit var btnLoad: Button
    private lateinit var pieChart: PieChart
    private var userId: Int = -1

    private val viewModel: ExpenseViewModel by viewModels {
        ViewModelFactory(AppRepository(AppDatabase.getInstance(this).appDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_summary)

        userId = intent.getIntExtra("userId", -1)

        etStart = findViewById(R.id.etStartDateSummary)
        etEnd = findViewById(R.id.etEndDateSummary)
        btnLoad = findViewById(R.id.btnLoadSummary)
        pieChart = findViewById(R.id.pieChart)

        val calendar = Calendar.getInstance()

        val pickDate = { editText: EditText ->
            DatePickerDialog(this,
                { _, y, m, d ->
                    val formattedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                    editText.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etStart.setOnClickListener { pickDate(etStart) }
        etEnd.setOnClickListener { pickDate(etEnd) }

        btnLoad.setOnClickListener {
            val start = etStart.text.toString()
            val end = etEnd.text.toString()

            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            viewModel.getExpenses(userId, start, end).observe(this) { expenses ->
                viewModel.getAllCategories().observe(this) { categories ->
                    renderPieChart(expenses, categories)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun renderPieChart(expenses: List<Expense>, categories: List<Category>) {
        if (expenses.isEmpty()) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show()
            pieChart.clear()
            return
        }


        val categoryMap = categories.associateBy({ it.id }, { it.name })


        val categorySums = HashMap<Int, Float>()
        for (expense in expenses) {
            val current = categorySums[expense.categoryId] ?: 0f
            categorySums[expense.categoryId] = current + expense.amount.toFloat()
        }


        val entries = categorySums.map { (categoryId, total) ->
            val name = categoryMap[categoryId] ?: "Unknown"
            PieEntry(total, name)
        }

        val dataSet = PieDataSet(entries, "Expense Summary")
        dataSet.setColors(
            ContextCompat.getColor(this, R.color.purple_200),
            ContextCompat.getColor(this, R.color.purple_500),
            ContextCompat.getColor(this, R.color.teal_200),
            ContextCompat.getColor(this, R.color.teal_700),
            ContextCompat.getColor(this, android.R.color.holo_orange_light)
        )
        dataSet.sliceSpace = 2f

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(14f)

        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        pieChart.animateY(800)
        pieChart.invalidate()
    }
}

