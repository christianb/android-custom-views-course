package com.techyourchance.androidviews.exercises._08_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import kotlin.math.max
import kotlin.math.min

class StatesProgressionView : CustomViewScaffold {

	private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		textAlign = Paint.Align.LEFT
		typeface = ResourcesCompat.getFont(context, R.font.assistantregular)
		textSize = dpToPx(TEXT_SIZE_DP)
	}

	private var colorActive = 0
	private var colorInactive = 0

	private val states = mutableListOf<State>()
	private var currentState: State? = null

	private val textBoundsRect = Rect()

	private var marginVertical = 0f
	private var marginHorizontal = 0f
	private var circleCenterX = 0f
	private var circleRadius = 0f
	private var circleToTextSpacing = 0f
	private var textLeftX = 0f
	private var textHeight = 0f
	private var stateEntryHeight = 0f
	private var statesSpacingVertical = 0f

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
		context,
		attrs,
		defStyleAttr,
		defStyleRes
	)

	init {
		setBackgroundColor(ContextCompat.getColor(context, R.color.gray_dark))
		colorActive = ContextCompat.getColor(context, R.color.green)
		colorInactive = ContextCompat.getColor(context, R.color.semi_transparent)

		marginVertical = dpToPx(MARGIN_VERTICAL_DP)
		marginHorizontal = dpToPx(MARGIN_HORIZONTAL_DP)
		circleCenterX = marginHorizontal + circleRadius
		circleRadius = dpToPx(CIRCLE_RADIUS_DP)
		circleToTextSpacing = dpToPx(CIRCLE_TO_TEXT_SPACING_HORIZONTAL_DP)
		textLeftX = circleCenterX + circleRadius + dpToPx(CIRCLE_TO_TEXT_SPACING_HORIZONTAL_DP)
		textHeight = paint.fontMetrics.descent - paint.fontMetrics.ascent
		stateEntryHeight = 2 * circleRadius
		statesSpacingVertical = dpToPx(STATES_SPACING_VERTICAL_DP)
	}

	private var selfHeight = 0f
	private var selfWidth = 0f

	fun bindStates(states: List<State>) {
		this.states.clear()
		this.states.addAll(states)
		requestLayout()
	}

	fun bindCurrentState(state: State?) {
		this.currentState = state
		invalidate()
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)

		selfHeight = 2 * marginVertical + // twice the vertical margin
				states.size * stateEntryHeight +
				(states.size - 1) * statesSpacingVertical  // n-1 times the margin between states

		selfWidth = 2 * marginHorizontal + 2 * circleRadius + circleToTextSpacing + getLongestStateNameLength()

		val desiredWidth: Int = MeasureSpec.getSize(widthMeasureSpec)
		val width: Int = when (MeasureSpec.getMode(widthMeasureSpec)) {
			MeasureSpec.EXACTLY -> desiredWidth // respect the size the parent requests
			MeasureSpec.AT_MOST -> min(desiredWidth, selfWidth.toInt())
			else -> selfWidth.toInt()
		}

		val desiredHeight: Int = MeasureSpec.getSize(heightMeasureSpec)
		val height: Int = when (MeasureSpec.getMode(heightMeasureSpec)) {
			MeasureSpec.EXACTLY -> desiredHeight
			MeasureSpec.AT_MOST -> min(desiredHeight, selfHeight.toInt())
			else -> selfHeight.toInt()
		}

		setMeasuredDimension(width, height) // must call this function!
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		val currentStateIndex = states.indexOf(currentState)

		states.forEachIndexed { index: Int, _: State ->
			paint.color = if (index <= currentStateIndex) colorActive else colorInactive

			val circleY = marginVertical + stateEntryHeight / 2 + index * (stateEntryHeight + statesSpacingVertical)
			canvas.drawCircle(circleCenterX, circleY, circleRadius, paint)

			val textX = circleCenterX + circleRadius + circleToTextSpacing

			paint.getTextBounds(states[index].name, 0, states[index].name.length, textBoundsRect)
			canvas.drawText(states[index].name, textX, circleY - textBoundsRect.exactCenterY(), paint)

		}

	}
	private fun getLongestStateNameLength(): Int {
		var longestStateNameLength = 0

		states.forEach { state ->
			paint.getTextBounds(state.name, 0, state.name.length, textBoundsRect)
			longestStateNameLength = max(longestStateNameLength, textBoundsRect.width())
		}
		return longestStateNameLength
	}

	companion object {
		private const val MARGIN_HORIZONTAL_DP = 25f
		private const val MARGIN_VERTICAL_DP = 25f
		private const val STATES_SPACING_VERTICAL_DP = 20f
		private const val CIRCLE_RADIUS_DP = 8f
		private const val CIRCLE_TO_TEXT_SPACING_HORIZONTAL_DP = 15f
		private const val TEXT_SIZE_DP = 20f
	}
}