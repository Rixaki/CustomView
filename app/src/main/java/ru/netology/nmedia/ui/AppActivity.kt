package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = findViewById<StatsView>(R.id.statsView)
        view.data = listOf(
            0.25F,
            0.1F,
            0.45F,
            0.30F,
        )

        /*
        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.anim_lection)
        )
         */
    }
}