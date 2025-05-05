package com.techyourchance.androidviews.exercises._01_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.exercises._03_.SliderChangeListener
import com.techyourchance.androidviews.general.extensions.MotionEventExtensions.distanceTo

class MySliderView : CustomViewScaffold {

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
		context,
		attrs,
		defStyleAttr,
		defStyleRes
	)

	var sliderChangeListener: SliderChangeListener? = null
	var value: Float = 0f

	private val paint = Paint()

	private var lineXLeft = 0f
	private var lineXRight = 0f
	private var lineY = 0f
	private var lineHeight = 0f

	private var circleX = 0f
	private var circleY = 0f
	private var circleRadius = 0f

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		val lineMarginHorizontal = dpToPx(LINE_MARGIN_HORIZONTAL_DP)

		lineXLeft = lineMarginHorizontal
		lineXRight = w - lineMarginHorizontal
		lineY = h * LINE_VERTICAL_POS_FRACTION
		lineHeight = dpToPx(LINE_HEIGHT)

		val lineLength = lineXRight - lineXLeft

		circleRadius = dpToPx(CIRCLE_RADIUS_DP)
		circleX = lineLength * CIRCLE_X_POS_FRACTION + lineMarginHorizontal
		circleY = lineY

		updateSliderValue(circleX)
	}

	private var isDrag = false
	private var lastEventX = 0f

	override fun onTouchEvent(event: MotionEvent): Boolean {
		return if (event.action == MotionEvent.ACTION_DOWN) {
			if (event.distanceTo(circleX, circleY) > circleRadius) return false
			isDrag = true
			lastEventX = event.x
			true
		} else if (isDrag && event.action == MotionEvent.ACTION_MOVE) {
			val dx = event.x - lastEventX
			if (circleX + dx < lineXLeft || circleX + dx > lineXRight) return true
			circleX += dx
			lastEventX = event.x
			updateSliderValue(circleX)
			invalidate()
			true
		} else {
			isDrag = false
			false
		}
	}

	private fun updateSliderValue(sliderXPos: Float) {
		val lineLength = lineXRight - lineXLeft
		value = (sliderXPos - lineXLeft) / lineLength
		sliderChangeListener?.onValueChanged(value)
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		paint.color = Color.LTGRAY
		paint.style = Paint.Style.STROKE
		paint.strokeWidth = lineHeight

		canvas.drawLine(
			/* startX = */ lineXLeft,
			/* startY = */ lineY,
			/* stopX = */ lineXRight,
			/* stopY = */ lineY,
			/* paint = */ paint
		)

		paint.color = ContextCompat.getColor(context, R.color.primary_variant)
		paint.style = Paint.Style.FILL

		canvas.drawCircle(
			/* cx = */ circleX,
			/* cy = */ circleY,
			/* radius = */ circleRadius,
			/* paint = */ paint
		)
	}

	companion object {
		private const val LINE_MARGIN_HORIZONTAL_DP = 20f
		private const val LINE_VERTICAL_POS_FRACTION = 0.7f
		private const val LINE_HEIGHT = 5f
		private const val CIRCLE_RADIUS_DP = 15f
		private const val CIRCLE_X_POS_FRACTION = 0.5f
	}
}