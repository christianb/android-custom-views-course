package com.techyourchance.androidviews.exercises._05_

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.solutions._05_.SolutionExercise5View
import kotlin.math.pow
import kotlin.math.sqrt

class MyCheckmarkView : CustomViewScaffold {

	private val paint = Paint().apply {
		color = ContextCompat.getColor(context, R.color.green)
		strokeWidth = LINE_SIZE_DP
		style = Paint.Style.STROKE
	}

	private var referenceCheckmarkPath = Path()
	private var animatedCheckmarkPath = Path()

	private var checkmarkShortSideLength = 0f

	private var scale = 1f
	private var scalePivotX = 0f
	private var scalePivotY = 0f

	private var animatorSet: AnimatorSet? = null

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
		context,
		attrs,
		defStyleAttr,
		defStyleRes
	)

	fun startAnimation(durationMs: Long) {
		val scaleValueAnimator = ValueAnimator.ofFloat(1f, 1.2f, 1f).apply {
			interpolator = AccelerateDecelerateInterpolator()
			duration = durationMs / 2
			addUpdateListener {
				scale = it.animatedValue as Float
				invalidate()
			}
		}

		val pathValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
			interpolator = LinearInterpolator()
			duration = durationMs
			addUpdateListener { updatePath(fraction = it.animatedValue as Float) }
		}

		animatorSet = AnimatorSet().apply {
			play(pathValueAnimator)
			play(scaleValueAnimator).after(durationMs * 3 / 4)
			start()
		}
	}

	private fun updatePath(fraction: Float) {
		val pathMeasure = PathMeasure(referenceCheckmarkPath, false)
		val totalPathLength = 3 * checkmarkShortSideLength
		animatedCheckmarkPath.reset()
		pathMeasure.getSegment(0f, fraction * totalPathLength, animatedCheckmarkPath, true)
		invalidate()
	}

	fun stopAnimation() {
		animatorSet?.cancel()
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		updateReferenceCheckmarkPath(w, h, minPadding = dpToPx(SolutionExercise5View.LINE_SIZE_DP))
	}

	private fun updateReferenceCheckmarkPath(viewWidth: Int, viewHeight: Int, minPadding: Float) {
		checkmarkShortSideLength = calculateCheckmarkShortSideLength(viewWidth, viewHeight, minPadding)
		val checkmarkWidth = sqrt(5f) * checkmarkShortSideLength
		val checkmarkHeight = 2 * checkmarkShortSideLength / sqrt(5f)
		val checkmarkTop = (viewHeight - checkmarkHeight) / 2
		val checkmarkLeft = (viewWidth - checkmarkWidth) / 2
		val pivotPointX = checkmarkLeft + sqrt(checkmarkShortSideLength.pow(2) - checkmarkHeight.pow(2))

		val startPoint = PointF(checkmarkLeft, checkmarkTop)
		val pivotPoint = PointF(pivotPointX, checkmarkTop + checkmarkHeight)
		val endPoint = PointF(checkmarkLeft + checkmarkWidth, checkmarkTop)

		scalePivotX = checkmarkLeft + checkmarkWidth / 2
		scalePivotY = checkmarkTop + checkmarkHeight / 2

		referenceCheckmarkPath.reset()
		referenceCheckmarkPath.moveTo(startPoint.x, startPoint.y)
		referenceCheckmarkPath.lineTo(pivotPoint.x, pivotPoint.y)
		referenceCheckmarkPath.lineTo(endPoint.x, endPoint.y)
	}

	private fun calculateCheckmarkShortSideLength(viewWidth: Int, viewHeight: Int, minPadding: Float): Float {
		val checkmarkShortSideLengthCandidate = sqrt((viewWidth - 2 * minPadding).pow(2) / 5)
		val checkMarkHeightCandidate = 2 * checkmarkShortSideLengthCandidate / sqrt(5f)

		if (checkMarkHeightCandidate <= viewHeight - 2 * minPadding) return checkmarkShortSideLengthCandidate
		return (viewHeight - 2 * minPadding) * sqrt(5f) / 2
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		canvas.scale(scale, scale, scalePivotX, scalePivotY)
		canvas.drawPath(animatedCheckmarkPath, paint)
	}

	companion object {
		const val LINE_SIZE_DP = 25f
	}
}