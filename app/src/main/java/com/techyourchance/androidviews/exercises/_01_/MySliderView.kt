package com.techyourchance.androidviews.exercises._01_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.BaseSavedState
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
	var circleXFraction: Float = CIRCLE_X_POS_FRACTION

	private val paint = Paint()

	private var lineXLeft = 0f
	private var lineXRight = 0f
	private var lineY = 0f
	private var lineHeight = 0f

	private var circleX = 0f
	private var circleY = 0f
	private var circleRadius = 0f

	private var lineLength = 0f

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		val lineMarginHorizontal = dpToPx(LINE_MARGIN_HORIZONTAL_DP)

		lineXLeft = lineMarginHorizontal
		lineXRight = w - lineMarginHorizontal
		lineY = h * LINE_VERTICAL_POS_FRACTION
		lineHeight = dpToPx(LINE_HEIGHT)

		lineLength = lineXRight - lineXLeft

		circleRadius = dpToPx(CIRCLE_RADIUS_DP)
		circleX = lineLength * circleXFraction + lineMarginHorizontal
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
		circleXFraction = (sliderXPos - lineXLeft) / lineLength
		sliderChangeListener?.onValueChanged(circleXFraction)
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

	override fun onSaveInstanceState(): Parcelable {
		return MySliderState(super.onSaveInstanceState(), circleXFraction)
	}

	override fun onRestoreInstanceState(state: Parcelable?) {
		if (state is MySliderState) {
			super.onRestoreInstanceState(state.superSavedState)
			circleXFraction = state.circleXFraction
		} else super.onRestoreInstanceState(state)
	}

	companion object {
		private const val LINE_MARGIN_HORIZONTAL_DP = 20f
		private const val LINE_VERTICAL_POS_FRACTION = 0.7f
		private const val LINE_HEIGHT = 5f
		private const val CIRCLE_RADIUS_DP = 15f
		private const val CIRCLE_X_POS_FRACTION = 0.5f
	}
}

private class MySliderState: BaseSavedState {
	val superSavedState: Parcelable?
	val circleXFraction: Float

	constructor(
		superSavedState: Parcelable?,
		circleXCenterFraction: Float,
	): super(superSavedState) {
		this.superSavedState = superSavedState
		this.circleXFraction = circleXCenterFraction
	}

	constructor(parcel: Parcel) : super(parcel) {
		this.superSavedState = parcel.readParcelable(BaseSavedState::class.java.classLoader)
		this.circleXFraction = parcel.readFloat()
	}

	override fun writeToParcel(out: Parcel, flags: Int) {
		super.writeToParcel(out, flags)
		out.writeParcelable(superSavedState, flags)
		out.writeFloat(circleXFraction)
	}

	companion object CREATOR : Parcelable.Creator<MySliderState> {
		override fun createFromParcel(parcel: Parcel): MySliderState {
			return MySliderState(parcel)
		}

		override fun newArray(size: Int): Array<MySliderState?> {
			return arrayOfNulls(size)
		}
	}
}