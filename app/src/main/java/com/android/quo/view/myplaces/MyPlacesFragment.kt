package com.android.quo.view.myplaces

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.view.PlacePreviewAdapter
import com.android.quo.viewmodel.PlacePreviewListViewModel
import com.android.quo.viewmodel.PlacePreviewListViewModel.FragmentType.MY_PLACES
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_my_places.floatingActionButton
import kotlinx.android.synthetic.main.fragment_my_places.placePreviewRecyclerView
import kotlinx.android.synthetic.main.fragment_my_places.swipeRefreshLayout

/**
 * Created by vitusortner on 05.11.17.
 */
class MyPlacesFragment : Fragment() {

    private lateinit var placePreviewListViewModel: PlacePreviewListViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? =
            inflater.inflate(R.layout.fragment_my_places, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.bottomNavigationView?.visibility = View.VISIBLE

        placePreviewListViewModel = ViewModelProviders.of(this)
                .get(PlacePreviewListViewModel().javaClass)

        observePlacePreviewList()
        setupSwipeRefresh()
        setupFloatingActionButton()
    }

    /**
     * Observe place preview list and set adapter for place preview recycler view
     */
    private fun observePlacePreviewList() {
        placePreviewListViewModel.getPlacePreviewList(MY_PLACES).observe(this, Observer { list ->
            list?.let {
                placePreviewRecyclerView.adapter = PlacePreviewAdapter(list)
                placePreviewRecyclerView.layoutManager = LinearLayoutManager(this.context)
            }
        })
    }

    /**
     * Update place preview list and stop refreshing animation
     */
    private fun setupSwipeRefresh() =
            swipeRefreshLayout.setOnRefreshListener {
                placePreviewListViewModel.updatePlacePreviewList(MY_PLACES)
                swipeRefreshLayout.isRefreshing = false
            }

    private fun setupFloatingActionButton() =
            floatingActionButton.setOnClickListener {
                Snackbar.make(floatingActionButton, "Floating action button clicked",
                        Snackbar.LENGTH_LONG)
                        .setAction("HIDE", { })
                        .show()
            }
}