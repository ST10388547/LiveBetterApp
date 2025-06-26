package com.example.livebetterapp.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import com.example.livebetterapp.data.models.BudgetGoal
import com.example.livebetterapp.repository.AppRepository
import com.example.livebetterapp.viewmodels.ExpenseViewModel
import com.example.livebetterapp.viewmodels.ViewModelFactory

class SetBudgetActivity : AppCompatActivity() {

    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnSave: Button
    private var userId: Int = -1

    private val viewModel: ExpenseViewModel by viewModels {
        ViewModelFactory(AppRepository(AppDatabase.getInstance(this).appDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_budget)


        userId = intent.getIntExtra("userId", -1)

        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        btnSave = findViewById(R.id.btnSaveBudget)

        viewModel.getBudgetGoal(userId).observe(this, Observer { goal ->
            goal?.let {
                etMinGoal.setText(it.minGoal.toString())
                etMaxGoal.setText(it.maxGoal.toString())
            }
        })

        btnSave.setOnClickListener {
            val minText = etMinGoal.text.toString()
            val maxText = etMaxGoal.text.toString()

            if (minText.isEmpty() || maxText.isEmpty()) {
                Toast.makeText(this, "Please enter both values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val goal = BudgetGoal(
                userId = userId,
                minGoal = minText.toDouble(),
                maxGoal = maxText.toDouble()
            )

            viewModel.insertOrUpdateBudgetGoal(goal)
            Toast.makeText(this, "Budget saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}
