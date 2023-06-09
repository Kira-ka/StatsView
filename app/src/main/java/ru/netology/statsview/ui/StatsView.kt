package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Xfermode
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R


class StatsView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {


    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private var lineWidth = AndroidUtils.dp(context, 5).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40).toFloat()
    private var colors = emptyList<Int>()


    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            fontSize = getDimension(R.styleable.StatsView_android_textSize, fontSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            colors = listOf(
                getColor(R.styleable.StatsView_colors1, generateRandomColor()),
                getColor(R.styleable.StatsView_colors2, generateRandomColor()),
                getColor(R.styleable.StatsView_colors3, generateRandomColor()),
                getColor(R.styleable.StatsView_colors4, generateRandomColor()),
            )
        }
    }

    private val paint = Paint(Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.MITER
        strokeCap = Paint.Cap.ROUND
    })

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        var startFrom = -90F
        for ((index, datum) in data.withIndex()) {
            val angle = countAngle(datum)
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startFrom + (360 * progress), angle * progress, false, paint)
            startFrom += angle
        }

        paint.color = colors.firstOrNull() ?: generateRandomColor()
        canvas.drawArc(oval, startFrom, 1F * progress, false, paint)

        canvas.drawText(
            "%.2f%%".format(data.sum() / 20),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius,
        )
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
            duration = 1000
            interpolator = LinearInterpolator()
        }.also { it.start() }
    }

    private fun countAngle(float: Float) = float / 5 * 0.0025F * 360F

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

}
