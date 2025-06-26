package com.example.livebetterapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import com.example.livebetterapp.data.models.Category
import com.example.livebetterapp.data.models.Expense
import com.example.livebetterapp.repository.AppRepository
import com.example.livebetterapp.viewmodels.ExpenseViewModel
import com.example.livebetterapp.viewmodels.ViewModelFactory
import kotlinx.coroutines.runBlocking
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var autoCategory: AutoCompleteTextView
    private lateinit var etAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var etTimeStart: EditText
    private lateinit var etTimeEnd: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnPickImage: Button
    private lateinit var btnSaveExpense: Button

    private var categories: List<Category> = listOf()
    private var photoUri: Uri? = null
    private var userId: Int = -1

    private val viewModel: ExpenseViewModel by viewModels {
        ViewModelFactory(AppRepository(AppDatabase.getInstance(this).appDao()))
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            photoUri = it
            imgPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)


        userId = intent.getIntExtra("userId", -1)

        autoCategory = findViewById(R.id.autoCategory)
        etAmount = findViewById(R.id.etAmount)
        etDescription = findViewById(R.id.etDescription)
        etDate = findViewById(R.id.etDate)
        etTimeStart = findViewById(R.id.etTimeStart)
        etTimeEnd = findViewById(R.id.etTimeEnd)
        imgPreview = findViewById(R.id.imgPreview)
        btnPickImage = findViewById(R.id.btnPickImage)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)

        val calendar = Calendar.getInstance()

        etDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d -> etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d)) },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etTimeStart.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                etTimeStart.setText(String.format("%02d:%02d", h, m))
            },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            ).show()
        }

        etTimeEnd.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                etTimeEnd.setText(String.format("%02d:%02d", h, m))
            },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            ).show()
        }

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        viewModel.getAllCategories().observe(this, Observer { list ->
            categories = list
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories.map { it.name })
            autoCategory.setAdapter(adapter)
        })

        btnSaveExpense.setOnClickListener {
            val categoryName = autoCategory.text.toString().trim()
            val amountText = etAmount.text.toString()
            val desc = etDescription.text.toString()
            val date = etDate.text.toString()
            val startTime = etTimeStart.text.toString()
            val endTime = etTimeEnd.text.toString()

            if (categoryName.isEmpty() || amountText.isEmpty() || desc.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoryId = categories.find { it.name.equals(categoryName, ignoreCase = true) }?.id
                ?: runBlocking {
                    viewModel.insertCategory(Category(name = categoryName)).toInt()
                }

            val expense = Expense(
                userId = userId,
                categoryId = categoryId,
                amount = amountText.toDouble(),
                description = desc,
                date = date,
                timeStart = startTime,
                timeEnd = endTime,
                photoUri = photoUri?.toString()
            )

            viewModel.insertExpense(expense)
            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
