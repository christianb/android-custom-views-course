package com.techyourchance.androidviews.demonstrations._10_path_arcs

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import kotlin.math.min

class PathArcView : CustomViewScaffold {

    private val rectPaint = Paint().apply {
        strokeWidth = dpToPx(LINE_SIZE_DP)
        color = ContextCompat.getColor(context, R.color.primary_variant)
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val arcPaint = Paint(rectPaint).apply {
        color = ContextCompat.getColor(context, R.color.green)
    }

    private val rectPath = Path()
    private val arcPath = Path()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val rectSize = min(w, h) / 2f
        val rect = RectF(
            (width - rectSize) / 2,
            (height - rectSize) / 2,
            width.toFloat() - (width - rectSize) / 2,
            (height + rectSize) / 2
        )

        rectPath.reset()
//        rectPath.addRect(rect, Path.Direction.CW)
//        rectPath.addRoundRect(rect, rectSize / 2, rectSize / 2, Path.Direction.CW)
        rectPath.addRoundRect(rect,
            floatArrayOf(
                0f, 0f,
                rectSize / 2, rectSize / 2,
                0f, 0f,
                0f, 0f,
            ),
            Path.Direction.CW)

        arcPath.reset()
        arcPath.addArc(rect, 0f, 270f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(rectPath, rectPaint)
//        canvas.drawPath(arcPath, arcPaint)
    }

    companion object {
        const val LINE_SIZE_DP = 5f
    }
}