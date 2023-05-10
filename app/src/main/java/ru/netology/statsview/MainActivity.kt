package ru.netology.statsview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<StatsView>(R.id.statsView)
        view.postDelayed({
            view.data = listOf(
                500F,
                500F,
                500F,
                500F
            )
        }, 3000)
    }

}
