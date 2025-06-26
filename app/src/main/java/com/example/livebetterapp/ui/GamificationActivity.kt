package com.example.livebetterapp.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.livebetterapp.R
import com.example.livebetterapp.data.db.AppDatabase
import kotlinx.coroutines.*
import java.util.*

class GamificationActivity : AppCompatActivity() {

    private lateinit var badgeImage: ImageView
    private lateinit var badgeText: TextView
    private val badgeThresholds = listOf(5, 10, 20) // Bronze, Silver, Gold

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamification)


        badgeImage = findViewById(R.id.badgeImage)
        badgeText = findViewById(R.id.badgeText)

        showUserBadge()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    private fun showUserBadge() {
        val expenseDao = AppDatabase.getInstance(this).appDao()

        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis()
            val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateString = sdf.format(Date(thirtyDaysAgo))
            val daysLogged = expenseDao.getLoggedDaysSince(startDateString).distinct().size

            withContext(Dispatchers.Main) {
                val (badge, message) = when {
                    daysLogged >= badgeThresholds[2] -> R.drawable.gold_badge to "Gold Badge: Amazing job!"
                    daysLogged >= badgeThresholds[1] -> R.drawable.silver_badge to "Silver Badge: Keep it up!"
                    daysLogged >= badgeThresholds[0] -> R.drawable.bronze_badge to "Bronze Badge: You're getting there!"
                    else -> R.drawable.no_badge to "No badge yet, log more!"
                }

                badgeImage.setImageResource(badge)
                badgeText.text = message
            }
        }
    }
}
