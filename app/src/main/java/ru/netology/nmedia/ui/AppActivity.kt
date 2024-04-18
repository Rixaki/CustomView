package ru.netology.nmedia.ui

import android.os.Bundle
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
        val rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0F, 360F)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0F, 1F)
        ObjectAnimator.ofPropertyValuesHolder(view, rotation, alpha)
            .apply {
                startDelay = 500
                duration = 500
                interpolator = LinearInterpolator()
            }.start()

         */
        /*
        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.anim_lection)
        )
         */
    }
}