package com.techyourchance.androidviews.general.navigation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.ncapdevi.fragnav.FragNavController
import com.techyourchance.androidviews.general.home.HomeFragment
import com.techyourchance.androidviews.demonstrations._01_basicshapes.BasicShapesFragment
import com.techyourchance.androidviews.exercises._01_.MySliderFragment
import com.techyourchance.androidviews.demonstrations._02_positioning.PositioningFragment
import com.techyourchance.androidviews.demonstrations._03_basic_touch.BasicTouchFragment
import com.techyourchance.androidviews.demonstrations._04_drag.DragFragment
import com.techyourchance.androidviews.demonstrations._05_state_preservation.StatePreservationFragment
import com.techyourchance.androidviews.demonstrations._06_animations.AnimationsFragment
import com.techyourchance.androidviews.demonstrations._07_path_shape.PathShapeFragment
import com.techyourchance.androidviews.demonstrations._08_path_animation.PathAnimationFragment
import com.techyourchance.androidviews.demonstrations._09_text.TextFragment
import com.techyourchance.androidviews.demonstrations._10_path_arcs.PathArcFragment
import com.techyourchance.androidviews.demonstrations._11_on_measure.OnMeasureFragment
import com.techyourchance.androidviews.demonstrations._12_matrix_transformation.MatrixTransformationFragment
import com.techyourchance.androidviews.demonstrations._14_gesture_detector.GestureDetectorFragment
import com.techyourchance.androidviews.demonstrations._15_scale_gesture_detector.ScaleGestureDetectorFragment
import com.techyourchance.androidviews.demonstrations._16_rotation_gesture_detector.RotationGestureDetectorFragment
import com.techyourchance.androidviews.exercises._05_.MyCheckmarkFragment
import com.techyourchance.androidviews.exercises._07_.CouponsFragment
import com.techyourchance.androidviews.exercises._08_.StatesProgressionFragment
import com.techyourchance.androidviews.exercises._09_.CrosshairFragment
import com.techyourchance.androidviews.exercises._10_.SmileyFragment
import com.techyourchance.androidviews.solutions._03_.SolutionExercise3Fragment


@UiThread
class ScreensNavigator constructor(
    private val activity: Activity,
    private val fragNavController: FragNavController,
    private val screenNameDelegate: ScreenNameDelegate,
    private val toolbarBackButtonDelegate: ToolbarBackButtonDelegate,
) {

    interface Listener {
        fun onScreenChanged()
    }

    fun init(savedInstanceState: Bundle?) {
        fragNavController.rootFragments = listOf(getRootFragment())
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    private fun getRootFragment(): Fragment {
        return DummyRootFragment()
    }

    fun onSaveInstanceState(saveInstanceState: Bundle) {
        fragNavController.onSaveInstanceState(saveInstanceState)
    }

    fun toScreen(screenSpec: ScreenSpec) {
        val nextFragment = when(screenSpec) {
            is ScreenSpec.Home -> HomeFragment.newInstance()
            is ScreenSpec.BasicShapes -> BasicShapesFragment.newInstance()
            is ScreenSpec.Exercise1 -> MySliderFragment.newInstance()
            is ScreenSpec.Positioning -> PositioningFragment.newInstance()
            is ScreenSpec.BasicTouch -> BasicTouchFragment.newInstance()
            is ScreenSpec.Drag -> DragFragment.newInstance()
            is ScreenSpec.SolutionExercise3 -> SolutionExercise3Fragment.newInstance()
            is ScreenSpec.StatePreservation -> StatePreservationFragment.newInstance()
            is ScreenSpec.Animations -> AnimationsFragment.newInstance()
            is ScreenSpec.PathShape -> PathShapeFragment.newInstance()
            is ScreenSpec.PathAnimation -> PathAnimationFragment.newInstance()
            is ScreenSpec.Exercise5 -> MyCheckmarkFragment.newInstance()
            is ScreenSpec.Text -> TextFragment.newInstance()
            is ScreenSpec.PathArc -> PathArcFragment.newInstance()
            is ScreenSpec.Exercise7 -> CouponsFragment.newInstance()
            is ScreenSpec.OnMeasure -> OnMeasureFragment.newInstance()
            is ScreenSpec.Exercise8 -> StatesProgressionFragment.newInstance()
            is ScreenSpec.MatrixTransformation -> MatrixTransformationFragment.newInstance()
            is ScreenSpec.Exercise9 -> CrosshairFragment.newInstance()
            is ScreenSpec.GestureDetector-> GestureDetectorFragment.newInstance()
            is ScreenSpec.ScaleGestureDetector-> ScaleGestureDetectorFragment.newInstance()
            is ScreenSpec.RotationGestureDetector -> RotationGestureDetectorFragment.newInstance()
            is ScreenSpec.Exercise10 -> SmileyFragment.newInstance()
        }
        toFragment(nextFragment)
        screenNameDelegate.clearScreenName()
    }

    fun navigateBack() {
        if (fragNavController.isRootFragment) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            activity.startActivity(homeIntent)
        } else {
            fragNavController.popFragment()
            if (fragNavController.isRootFragment) {
                toolbarBackButtonDelegate.hideBackButton()
            }
        }
    }

    private fun toFragment(fragment: Fragment) {
        if (shouldClearFragmentsStack(fragment)) {
            fragNavController.clearStack()
            fragNavController.replaceFragment(fragment)
            toolbarBackButtonDelegate.hideBackButton()
        } else {
            fragNavController.pushFragment(fragment)
            toolbarBackButtonDelegate.showBackButton()
        }
    }

    private fun shouldClearFragmentsStack(nextFragment: Fragment): Boolean {
        val currentFragment = fragNavController.currentFrag ?: return false
        return currentFragment is DummyRootFragment
                || nextFragment is HomeFragment
    }

}