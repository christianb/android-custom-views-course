package com.techyourchance.androidviews.exercises._01_

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.techyourchance.androidviews.general.BaseFragment
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.exercises._03_.SliderChangeListener

class MySliderFragment : BaseFragment(), SliderChangeListener {

	override val screenName get() = getString(R.string.screen_name_my_slider)

	private lateinit var sliderText: TextView
	private lateinit var mySlider: MySliderView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		return layoutInflater.inflate(R.layout.layout_my_slider, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		sliderText = view.findViewById(R.id.slider_position)
		mySlider = view.findViewById(R.id.my_slider)
		mySlider.sliderChangeListener = this
	}

	override fun onValueChanged(value: Float) {
		sliderText.text = value.toString()
	}

	override fun onStart() {
		super.onStart()
		sliderText.text = mySlider.circleXFraction.toString()
	}

	companion object {
		fun newInstance(): MySliderFragment {
			return MySliderFragment()
		}
	}
}