# Android Custom Views Course

This repo contains the source code of the tutorial application for [Android Custom Views Course](https://www.techyourchance.com/courses/android-custom-views/).

## 1. Basics

### 1.1 Creating a Custom View
When creating a custom View you need to extend the class `View`.
Then you implement the default constructors.

There are two important functions you have to override.
1. `onSizeChanged(w, h, oldW, oldH)` - will be called when this view changes its size. This is the place to init all values that depend on the size of the view.
2. `onDraw(c)` - will be called when this view needs to redraw itself.

### 1.2 Coordinate System
The coordinate system origin `(0,0)` is located at the top-left of the screen.
![image](./screenshots/coordinate-system.png)

Nested views x and y coordinates are measured relative to its parent's origin.
![image](./screenshots/nested-view-coordinates.png)

### 1.3 Density Independent Pixel
A size of a pixel varies between different screens.
In some cases, UI element should have specific physical size, regardless of screen's characteristics.
- `1dp = ~0.16 mm`

### 1.4 Best Practices
`onDraw` should not contain any computational intense logic. `onDraw` should return as quickly as possible. Instead move any computations into `onSizeChanged`.

General util function to convert dp into px:
```kotlin
fun dpToPx(dp: Float): Float {
	return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
	)
}
```

### 1.5 Basic Shape: Line
To draw anything we need an instance of `Paint`.
```kotlin
class CustomLine : View {
	// omitting default constructors

	private val paint = Paint()
    
    private var lineXLeft = 0f
	private var lineXRight = 0f
	private var lineYPos = 0f
	private var lineHeight = 0f
    
    fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
		super.onSizeChanged(w, h, oldW, oldH)
        val lineMarginHorizontal = dpToPx(LINE_MARGIN_HORIZONTAL_DP)
        lineXLeft = lineMarginHorizontal
        lineXRight = w - lineMarginHorizontal
        lineYPos = h * LINE_VERTICAL_POS_FRACTION
		lineHeight = dpToPx(LINE_HEIGHT_DP)
	}

	override fun onDraw(canvas: Canvas) {
		paint.color = Color.Red
		paint.style = Paint.Style.STROKE
		paint.strokeWidth = dpToPx(LINE_HEIGHT_DP)
		canvas.drawLine(
            startX = lineXLeft, 
            startY = lineYPos, 
            stopX = lineXRight,
			stopY = lineYPos, 
            paint
		)
	}
    
    companion object {
		const val LINE_HEIGHT_DP = 2f
        const val LINE_MARGIN_HORIZONTAL_DP = 20f
        const val LINE_VERTICAL_POS_FRACTION = 0.3f
	}
}
```

### 1.6 Basic Shape: Rectangle
```kotlin
class CustomLine : View {
	// omitting default constructors

	private val paint = Paint()
    private val rectangle = RectF()
    
    fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
		super.onSizeChanged(w, h, oldW, oldH)
		val rectangleMarginHorizontal = dpToPx(RECT_MARGIN_HORIZONTAL_DP)
        val rectangleWidth = w - 2 * rectangleMarginHorizontal
        val rectangleHeight = rectangleWidth / 2
        rectangle.set(
			left = rectangleMarginHorizontal,
			top = (h - rectangleHeight) / 2,
			right = rectangleMarginHorizontal + rectangleWidth,
			bottom = (h + rectangleHeight) / 2
		)
	}

	override fun onDraw(canvas: Canvas) {
		paint.color = Color.Red
		paint.style = Paint.Style.STROKE // or FILL to fill the rect
		paint.strokeWidth = dpToPx(LINE_HEIGHT_DP)
		canvas.drawRect(rectangle, paint)
	}
    
    companion object {
		const val LINE_HEIGHT_DP = 2f
        const val RECT_MARGIN_HORIZONTAL_DP = 20f
	}
}
```

### 1.7 Basic Shape: Circle
```kotlin
class CustomLine : View {
	// omitting default constructors

	private val paint = Paint()
    private var circleXCenter = 0f
    private var circleYCenter = 0f
    private var circleRadius = 0f
    
    fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
		super.onSizeChanged(w, h, oldW, oldH)
		circleXCenter = w / 2f
        circleYCenter = h * CIRCLE_HEIGHT_POS_FRACTION
        circleRadius = h * CIRCLE_RADIUS_FRACTION
        
	}

	override fun onDraw(canvas: Canvas) {
		paint.color = Color.Red
		paint.style = Paint.Style.STROKE // or FILL to fill the rect
		paint.strokeWidth = dpToPx(LINE_HEIGHT_DP)
		canvas.drawCircle(
            cx = circleXCenter, 
            cy = circleYCenter, 
            radius = circleRadius, 
            paint
		)
	}
    
    companion object {
		const val LINE_HEIGHT_DP = 2f
        const val CIRCLE_HEIGHT_POS_FRACTION = 0.8f
        const val CIRCLE_RADIUS_FRACTION = 0.1f
	}
}
```

### 1.8 Touch Events
Handling touch events in a custom view just override `onTouchEvent(event: MotionEvent?): Boolean`.

### 1.9 View State Preservation on Config Change (and Process Death)
When the system determines that the view must save its state it calls `onSaveInstanceState()`.

Similarly to restore a view state you need to override `onRestoreInstanceState()`.

To save the state of the view you create a new State class that extends `View.BaseSavedState`.

To allow the system calling the functions above, you __must ensure__ that your created view as some unique id.
```kotlin
override fun onCreateView(...): View {
	return MyView(context).apply { id = R.id.my_view }
}
```

## 2. Animations