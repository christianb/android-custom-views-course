package com.techyourchance.androidviews.general.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techyourchance.androidviews.general.BaseFragment
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.general.navigation.ScreenSpec

class HomeFragment : BaseFragment() {

    private lateinit var destinationsRecycler: RecyclerView
    private lateinit var destinationsAdapter: DestinationsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = layoutInflater.inflate(R.layout.layout_home, container, false).apply {
            destinationsRecycler = findViewById(R.id.recycler)
            destinationsRecycler.layoutManager = LinearLayoutManager(context)
            destinationsAdapter = DestinationsAdapter(context)
            destinationsRecycler.adapter = destinationsAdapter
        }
        initDestinations()
        return view
    }

    private fun initDestinations() {
        val fromHomeDestinations = listOf(
            FromHomeDestination(
                ScreenSpec.BasicShapes,
                getString(R.string.screen_name_basic_shapes),
            ),
            FromHomeDestination(
                ScreenSpec.Exercise1,
                getString(R.string.screen_name_my_slider),
            ),
            FromHomeDestination(
                ScreenSpec.Positioning,
                getString(R.string.screen_name_positioning),
            ),
            FromHomeDestination(
                ScreenSpec.BasicTouch,
                getString(R.string.screen_name_basic_touch),
            ),
            FromHomeDestination(
                ScreenSpec.Drag,
                getString(R.string.screen_name_drag),
            ),
            FromHomeDestination(
                ScreenSpec.SolutionExercise3,
                getString(R.string.screen_name_solution_exercise_3),
            ),
            FromHomeDestination(
                ScreenSpec.StatePreservation,
                getString(R.string.screen_name_state_preservation),
            ),
            FromHomeDestination(
                ScreenSpec.Animations,
                getString(R.string.screen_name_animations),
            ),
            FromHomeDestination(
                ScreenSpec.PathShape,
                getString(R.string.screen_name_path_shape),
            ),
            FromHomeDestination(
                ScreenSpec.PathAnimation,
                getString(R.string.screen_name_path_animation),
            ),
            FromHomeDestination(
                ScreenSpec.Exercise5,
                getString(R.string.screen_name_my_checkmark),
            ),
            FromHomeDestination(
                ScreenSpec.Text,
                getString(R.string.screen_name_text),
            ),
            FromHomeDestination(
                ScreenSpec.PathArc,
                getString(R.string.screen_name_path_arc),
            ),
            FromHomeDestination(
                ScreenSpec.Exercise7,
                getString(R.string.screen_name_coupons),
            ),
            FromHomeDestination(
                ScreenSpec.OnMeasure,
                getString(R.string.screen_name_on_measure),
            ),
            FromHomeDestination(
                ScreenSpec.Exercise8,
                getString(R.string.screen_name_states),
            ),
            FromHomeDestination(
                ScreenSpec.MatrixTransformation,
                getString(R.string.screen_name_matrix_transformation),
            ),
            FromHomeDestination(
                ScreenSpec.Exercise9,
                getString(R.string.screen_name_crosshair),
            ),
            FromHomeDestination(
                ScreenSpec.GestureDetector,
                getString(R.string.screen_name_gesture_detector),
            ),
            FromHomeDestination(
                ScreenSpec.ScaleGestureDetector,
                getString(R.string.screen_name_scale_gesture_detector),
            ),
            FromHomeDestination(
                ScreenSpec.RotationGestureDetector,
                getString(R.string.screen_name_rotation_gesture_detector),
            ),
            FromHomeDestination(
                ScreenSpec.Exercise10,
                getString(R.string.screen_name_smiley),
            ),
        )
        destinationsAdapter.bindDestinations(fromHomeDestinations)
    }

    private fun onDestinationClicked(destination: FromHomeDestination) {
        screensNavigator.toScreen(destination.screenSpec)
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private inner class DestinationsAdapter(
        private val context: Context,
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val destinations = mutableListOf<FromHomeDestination>()

        fun bindDestinations(destinationDetails: List<FromHomeDestination>) {
            this.destinations.clear()
            this.destinations.addAll(destinationDetails)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return destinations.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_home_destination_item, parent, false)
            return DestinationViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            if (viewHolder !is DestinationViewHolder) {
                throw RuntimeException("unexpected ViewHolder type")
            }
            val destination = destinations[position]
            viewHolder.txtDestinationTitle.text = destination.title
            viewHolder.view.setOnClickListener {
                onDestinationClicked(destination)
            }
            viewHolder.view.updateLayoutParams<MarginLayoutParams> {
                topMargin = if (position == 0) {
                    0 // remove top margin from the first item (RV should have padding)
                } else {
                    topMargin
                }
            }
        }
    }

    class DestinationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtDestinationTitle: TextView = view.findViewById(R.id.txtDestinationTitle)
    }
}