package com.example.livebetterapp.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import com.example.livebetterapp.data.models.User
import com.example.livebetterapp.repository.AppRepository
import com.example.livebetterapp.viewmodels.UserViewModel
import com.example.livebetterapp.viewmodels.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var etNewUsername: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var btnRegister: Button

    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory(AppRepository(AppDatabase.getInstance(this).appDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNewUsername = findViewById(R.id.etNewUsername)
        etNewPassword = findViewById(R.id.etNewPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val username = etNewUsername.text.toString()
            val password = etNewPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = User(username = username, password = password)
                viewModel.register(user)
                Toast.makeText(this, "Registered Successfully. Please login.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
