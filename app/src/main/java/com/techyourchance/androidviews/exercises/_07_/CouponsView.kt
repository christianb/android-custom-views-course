package com.techyourchance.androidviews.exercises._07_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R

class CouponsView : CustomViewScaffold {

	private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = ContextCompat.getColor(context, R.color.primary_variant)
		style = Paint.Style.STROKE
	}

	private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		textAlign = Paint.Align.CENTER
		isFakeBoldText = true
		color = ContextCompat.getColor(context, R.color.gray_dark)

	}

	private var totalCoupons = 0
	private var usedCoupons = 0

	private var borderLineSize = 0f

	private var cellWidth = 0f
	private val borderPath = Path()

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
		context,
		attrs,
		defStyleAttr,
		defStyleRes
	)

	fun setCoupons(totalCoupons: Int, usedCoupons: Int) {
		this.totalCoupons = totalCoupons
		this.usedCoupons = usedCoupons
		if (width > 0 && height > 0) {
			updateBorderPath()
			updateTextSize()
			invalidate()
		}
	}

	private fun getCornerRadiusArrayForCell(cellIndex: Int, cornerRadius: Float): FloatArray {
		return when (cellIndex) {
			0 -> floatArrayOf(
				0f, 0f, // top-left
				0f, 0f, // top-right
				0f, 0f, // bottom-right
				cornerRadius, cornerRadius,  // bottom-left
			)

			totalCoupons - 1 -> floatArrayOf(
				// the order depends on Path.Direction.CW (clock-wise) or Path.Direction.CCW (counter-clock-wise)
				0f, 0f, // top-left
				0f, 0f, // makes the top-right corner round, rectSize / 2 == radius
				cornerRadius, cornerRadius,  // bottom-right
				0f, 0f, // bottom-left
			)

			else -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
		}
	}

	private fun updateBorderPath() {
		val borderLinePadding = borderLineSize / 2
		cellWidth = (width.toFloat() - 2 * borderLinePadding) / totalCoupons

		var left = borderLinePadding
		var right = left + cellWidth

		borderPath.reset()
		for (i in 0 until totalCoupons) {
			borderPath.addRoundRect(
				RectF(/* left = */ left, /* top = */ borderLinePadding,
					/* right = */ right, /* bottom = */ height.toFloat() - borderLinePadding),
				/* radii = */ getCornerRadiusArrayForCell(i, cornerRadius = dpToPx(CORNER_RADIUS_DP)),
				/* dir = */ Path.Direction.CW
			)
			left = right
			right = left + cellWidth
		}
	}

	private fun updateTextSize() {
		val minPadding = dpToPx(CELL_MIN_PADDING_DP)
		val maxTextWidth = cellWidth - 2 * (minPadding + borderLineSize)
		val maxTextHeight = height.toFloat() - 2 * (minPadding + borderLineSize)

		val longestNumOfCouponsText = "99" // assume double-digit number of coupons at most

		var size = 1f
		val bounds = Rect()

		// Binary search can be used for better performance
		while (true) {
			textPaint.textSize = size
			textPaint.getTextBounds(longestNumOfCouponsText, 0, longestNumOfCouponsText.length, bounds)
			if (bounds.width() > maxTextWidth || bounds.height() > maxTextHeight) {
				textPaint.textSize = size - 1
				return
			}
			size++
		}
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		borderLineSize = dpToPx(LINE_SIZE_DP)


		cellPaint.strokeWidth = borderLineSize

		updateBorderPath()
		updateTextSize()
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		canvas.drawPath(borderPath, cellPaint)

		val textHeight = textPaint.descent() - textPaint.ascent()
		val textY = height / 2f + textHeight / 2f
		for (i in 0 until totalCoupons) {
			val textX = cellWidth * (i + 0.5f)
			textPaint.alpha = if (i <= usedCoupons - 1) {
				80
			} else {
				255
			}
			canvas.drawText((i + 1).toString(), textX, textY, textPaint)
		}
	}

	companion object {
		private const val LINE_SIZE_DP = 2f
		private const val CELL_MIN_PADDING_DP = 5f
		private const val CORNER_RADIUS_DP = 20f
	}
}