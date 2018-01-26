package com.android.quo.view.myplaces

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.di.Injection
import com.android.quo.view.PlacePreviewAdapter
import com.android.quo.view.createplace.CreatePlaceFragment
import com.android.quo.viewmodel.MyPlacesViewModel
import com.android.quo.viewmodel.factory.MyPlacesViewModelFactory
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_my_places.floatingActionButton
import kotlinx.android.synthetic.main.fragment_my_places.recyclerView
import kotlinx.android.synthetic.main.fragment_my_places.swipeRefreshLayout

/**
 * Created by vitusortner on 05.11.17.
 */
class MyPlacesFragment : Fragment() {

    private lateinit var viewModel: MyPlacesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders
                .of(this, MyPlacesViewModelFactory(Injection.placeRepository, Injection.userRepository))
                .get(MyPlacesViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.bottomNavigationView?.visibility = View.VISIBLE

        observePlaces()
        setupSwipeRefresh()
        setupFloatingActionButton()
    }

    /**
     * Observe place preview list and set adapter for place preview recycler view
     */
    private fun observePlaces() {
        viewModel.getPlaces().observe(this, Observer {
            it?.let { list ->
                activity?.let { activity ->
                    recyclerView.adapter = PlacePreviewAdapter(list, activity.supportFragmentManager)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        })
    }

    /**
     * Update place preview list and stop refreshing animation
     */
    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.updatePlaces()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupFloatingActionButton() =
            floatingActionButton.setOnClickListener {
                fragmentManager?.beginTransaction()
                        ?.replace(R.id.content, CreatePlaceFragment())
                        ?.addToBackStack(null)
                        ?.commit()
            }
}