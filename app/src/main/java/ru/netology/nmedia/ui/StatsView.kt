package ru.netology.nmedia.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.util.AndroidUtils
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()

    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null

    private var animOption: Int = 0//will take to another link by app xml

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            val resId = getResourceId(R.styleable.StatsView_colors, 0)
            colors = resources.getIntArray(resId).toList()
            animOption = getInteger(R.styleable.StatsView_animation, animOption)
            //println("num anim: $animOption")
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            if (animOption == 0) {
                invalidate()//onDraw calls
            } else {
                update()
            }
        }
    //val drawStatusFlags = ArrayList<Boolean>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }
        val sumOfValues = data.sum()

        var startFrom = -90F
        val maxAngle = -90F + 360F * progress

        var firstColor = randomColor()
        var prelastColor = randomColor()
        var prelastArcEnd = 0F

        //drawText
        fun predicate(cancelIndex: Int): (index: Int, _: Any) -> Boolean =
            { index, _ -> index != cancelIndex }

        val ratio = 100 *
                data.filterIndexed(predicate(data.lastIndex)).sum() / data.sum()
        canvas.drawText(
            "%.2f%%".format(ratio),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )

        //cycle
        for ((index, datum) in data.withIndex()) {
            val angle = when (animOption) {
                3 -> { //sequential
                    min(
                        360F * datum / sumOfValues,
                        maxAngle - startFrom
                    )
                }

                else -> {
                    360F * datum / sumOfValues
                }
            }
            paint.color = colors.getOrNull(index) ?: randomColor()

            //data for first dot
            if (index == 0) {
                firstColor = paint.color
            }

            when (animOption) {
                0 -> { //no_animation
                    canvas.drawArc(
                        oval,
                        startFrom,
                        angle,
                        false,
                        paint
                    )
                }

                1 -> { //lection
                    canvas.drawArc(
                        oval,
                        startFrom,
                        angle * progress,
                        false,
                        paint
                    )
                }

                2 -> { //rotation
                    canvas.drawArc(
                        oval,
                        startFrom - 360F * (1F - progress),
                        angle * progress,
                        false,
                        paint
                    )
                }

                3 -> { //sequential
                    canvas.drawArc(
                        oval,
                        startFrom,
                        angle,
                        false,
                        paint
                    )
                }

                4 -> { //bidirectional
                    canvas.drawArc(
                        oval,
                        startFrom + angle * 0.5F,
                        angle * progress * 0.5F,
                        false,
                        paint
                    )
                    canvas.drawArc(
                        oval,
                        startFrom + angle * 0.5F,
                        -angle * progress * 0.5F,
                        false,
                        paint
                    )
                }
            }

            startFrom += angle

            //data for prelast dot
            if ((data.size >= 2) && (index == data.size - 2)) {
                prelastColor = paint.color
                prelastArcEnd = startFrom * Math.PI.toFloat() / 180F
            }

            //sequential
            if ((startFrom > maxAngle) && (animOption == 3)) {
                return
            }
        }

        //first dot
        if ((animOption == 1) && (progress == 1F)) {
            paint.color = firstColor
            canvas.drawPoint(center.x, center.y - radius, paint)
        }

        //prelast dot
        if ((data[data.lastIndex] > 0F) &&
            (progress == 1F) && (animOption == 1)) {
            paint.color = prelastColor
            canvas.drawPoint(
                center.x + radius * cos(prelastArcEnd),
                center.y + radius * sin(prelastArcEnd),
                paint
            )
        }
    }

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 1500
            interpolator = LinearInterpolator()
        }.also {
            it.start()
        }
    }

    private fun randomColor() =
        Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}