package com.example.livebetterapp.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import com.example.livebetterapp.repository.AppRepository
import com.example.livebetterapp.viewmodels.ExpenseViewModel
import com.example.livebetterapp.viewmodels.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var btnFilter: Button
    private lateinit var listExpenses: ListView

    private var userId: Int = -1
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val viewModel: ExpenseViewModel by viewModels {
        ViewModelFactory(AppRepository(AppDatabase.getInstance(this).appDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)


        userId = intent.getIntExtra("userId", -1)

        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        btnFilter = findViewById(R.id.btnFilter)
        listExpenses = findViewById(R.id.listExpenses)

        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val calendar = Calendar.getInstance()

        val showDatePicker: (EditText) -> Unit = { editText ->
            val dp = DatePickerDialog(this,
                { _, y, m, d ->
                    val formattedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                    editText.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
            dp.show()
        }

        etStartDate.setOnClickListener { showDatePicker(etStartDate) }
        etEndDate.setOnClickListener { showDatePicker(etEndDate) }

        btnFilter.setOnClickListener {
            val start = etStartDate.text.toString()
            val end = etEndDate.text.toString()

            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("FILTER", "Fetching expenses for userId=$userId from $start to $end")

            viewModel.getExpenses(userId, start, end).observe(this, Observer { expenses ->
                if (expenses.isEmpty()) {
                    Toast.makeText(this, "No expenses found in this date range", Toast.LENGTH_SHORT).show()
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, expenses.map {
                    "${it.date}: R${it.amount} - ${it.description}"
                })
                listExpenses.adapter = adapter

                listExpenses.setOnItemClickListener { _, _, position, _ ->
                    val selected = expenses[position]
                    val message = """
                        Description: ${selected.description}
                        Amount: R${selected.amount}
                        Category ID: ${selected.categoryId}
                        Time: ${selected.timeStart} to ${selected.timeEnd}
                    """.trimIndent()

                    if (selected.photoUri != null) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(Uri.parse(selected.photoUri), "image/*")
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}
