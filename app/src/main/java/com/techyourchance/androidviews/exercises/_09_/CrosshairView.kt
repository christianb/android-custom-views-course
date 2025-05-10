package com.techyourchance.androidviews.exercises._09_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View.BaseSavedState
import androidx.core.content.ContextCompat
import androidx.core.graphics.withSave
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import kotlin.math.sqrt

class CrosshairView : CustomViewScaffold {

	private val dotColor = ContextCompat.getColor(context, R.color.red)
	private val crosshairColor = ContextCompat.getColor(context, R.color.primary_variant)

	private val crosshairPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		strokeWidth = dpToPx(CROSSHAIR_LINE_SIZE_DP)
		style = Paint.Style.STROKE
		color = crosshairColor
	}

	private var crosshairCircleRadius = dpToPx(CROSSHAIR_CIRCLE_RADIUS_DP)
	private var crosshairDotRadius = dpToPx(CROSSHAIR_DOT_RADIUS_DP)
	private var crosshairHairLength = dpToPx(CROSSHAIR_HAIR_LENGTH_DP)

	private var crosshairXFraction = 0.5f
	private var crosshairYFraction = 0.5f

	private val crosshairDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = dotColor
	}

	private var isDragged = false
	private var lastMotionEventX = 0f
	private var lastMotionEventY = 0f

	private val crosshairXCenter: Float
		get() = width * crosshairXFraction

	private val crosshairYCenter: Float
		get() = height * crosshairYFraction

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
	}

	override fun onTouchEvent(event: MotionEvent): Boolean {
		val distanceFromCircleCenter = pointsDistance(event.x, event.y, crosshairXCenter, crosshairYCenter)
		return if (distanceFromCircleCenter <= crosshairCircleRadius && event.action == MotionEvent.ACTION_DOWN) {
			isDragged = true
			lastMotionEventX = event.x
			lastMotionEventY = event.y
			true
		} else if (isDragged && event.action == MotionEvent.ACTION_MOVE) {
			val dx: Float = event.x - lastMotionEventX
			val dy: Float = event.y - lastMotionEventY

			crosshairXFraction = (width * crosshairXFraction + dx) / width
			crosshairYFraction = (height * crosshairYFraction + dy) / height

			lastMotionEventX = event.x
			lastMotionEventY = event.y
			invalidate()
			true
		} else {
			isDragged = false
			false
		}
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		canvas.save() // not mandatory but ggood practice
		canvas.drawCircle(crosshairXCenter, crosshairYCenter, crosshairDotRadius, crosshairDotPaint)
		canvas.drawCircle(crosshairXCenter, crosshairYCenter, crosshairCircleRadius, crosshairPaint)

		canvas.translate(crosshairXCenter, crosshairYCenter)

		canvas.withSave {
			canvas.rotate(45f)
			for (i in 0 until 4) {
				canvas.rotate(i * 90f)
				canvas.drawLine(crosshairCircleRadius - crosshairHairLength / 2, 0f,
					crosshairCircleRadius + crosshairHairLength / 2, 0f, crosshairPaint)
			}
		}

		canvas.restore() // not mandatory but ggood practice
	}

	override fun onSaveInstanceState(): Parcelable {
		val superSavedState = super.onSaveInstanceState()
		return CrosshairSavedState(superSavedState, crosshairXFraction, crosshairYFraction)
	}

	override fun onRestoreInstanceState(state: Parcelable) {
		if (state is CrosshairSavedState) {
			super.onRestoreInstanceState(state.superSavedState)
			crosshairXFraction = state.crosshairXFraction
			crosshairYFraction = state.crosshairYFraction
			invalidate()
		} else super.onRestoreInstanceState(state)
	}

	companion object {
		private const val CROSSHAIR_CIRCLE_RADIUS_DP = 40f
		private const val CROSSHAIR_DOT_RADIUS_DP = 4f
		private const val CROSSHAIR_HAIR_LENGTH_DP = 20f
		private const val CROSSHAIR_LINE_SIZE_DP = 2f

		/**
		 * Compute the Euclidean distance between two points using the Pythagorean theorem
		 */
		private fun pointsDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
			val dx = x1 - x2
			val dy = y1 - y2
			return sqrt(dx * dx + dy * dy)
		}
	}
}

private class CrosshairSavedState : BaseSavedState {

	val superSavedState: Parcelable?
	val crosshairXFraction: Float
	val crosshairYFraction: Float

	constructor(
		superSavedState: Parcelable?,
		crosshairXFraction: Float,
		crosshairYFraction: Float,
	) : super(superSavedState) {
		this.superSavedState = superSavedState
		this.crosshairXFraction = crosshairXFraction
		this.crosshairYFraction = crosshairYFraction
	}

	constructor(parcel: Parcel) : super(parcel) {
		this.superSavedState = parcel.readParcelable(null)
		this.crosshairXFraction = parcel.readFloat()
		this.crosshairYFraction = parcel.readFloat()
	}

	override fun writeToParcel(out: Parcel, flags: Int) {
		super.writeToParcel(out, flags)
		out.writeParcelable(superSavedState, flags)
		out.writeFloat(crosshairXFraction)
		out.writeFloat(crosshairYFraction)
	}

	companion object CREATOR : Parcelable.Creator<CrosshairSavedState> {
		override fun createFromParcel(parcel: Parcel): CrosshairSavedState {
			return CrosshairSavedState(parcel)
		}

		override fun newArray(size: Int): Array<CrosshairSavedState?> {
			return arrayOfNulls(size)
		}
	}
}