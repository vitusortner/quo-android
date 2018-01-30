package com.android.quo.view.myplaces

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.view.home.PlacePreviewAdapter
import com.android.quo.view.createplace.CreatePlaceFragment
import com.android.quo.viewmodel.MyPlacesViewModel
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_my_places.floatingActionButton
import kotlinx.android.synthetic.main.fragment_my_places.recyclerView
import kotlinx.android.synthetic.main.fragment_my_places.swipeRefreshLayout
import org.koin.android.architecture.ext.getViewModel

/**
 * Created by vitusortner on 05.11.17.
 */
class MyPlacesFragment : Fragment() {

    private lateinit var viewModel: MyPlacesViewModel

    private lateinit var adapter: PlacePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            it.bottomNavigationView?.visibility = View.VISIBLE

            adapter = PlacePreviewAdapter(it.supportFragmentManager)
        }

        viewModel = getViewModel()
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

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        observePlaces()
        setupSwipeRefresh()
        setupFloatingActionButton()
    }

    /**
     * Observe place preview list and set adapter for place preview recycler view
     */
    private fun observePlaces() {
        viewModel.getPlaces().observe(this, Observer {
            it?.let { places ->
                adapter.setItems(places)
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