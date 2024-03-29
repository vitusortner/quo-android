package com.android.quo.view.myplaces

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.quo.R
import com.android.quo.util.Constants.FragmentTag
import com.android.quo.util.extension.createAndReplaceFragment
import com.android.quo.util.extension.filterNull
import com.android.quo.util.extension.observeWithLifecycle
import com.android.quo.view.BaseFragment
import com.android.quo.view.createplace.CreatePlaceFragment
import com.android.quo.view.home.HomeFragment
import com.android.quo.view.home.PlacePreviewAdapter
import com.android.quo.viewmodel.MyPlacesViewModel
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_my_places.floatingActionButton
import kotlinx.android.synthetic.main.fragment_my_places.recyclerView
import kotlinx.android.synthetic.main.fragment_my_places.swipeRefreshLayout
import org.koin.android.architecture.ext.viewModel

/**
 * Created by vitusortner on 05.11.17.
 */
class MyPlacesFragment : BaseFragment(R.layout.fragment_my_places) {

    private val viewModel by viewModel<MyPlacesViewModel>()

    private lateinit var adapter: PlacePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE

        adapter =
            PlacePreviewAdapter(imageLoader) { HomeFragment.onClick(it, requireFragmentManager()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        observe()
        setupSwipeRefresh()
        setupFloatingActionButton()
    }

    /**
     * Observe place preview list and set adapter for place preview recycler view
     */
    private fun observe() =
        viewModel.getPlaces()
            .filterNull()
            .observeWithLifecycle(this, adapter::setItems)

    /**
     * Update place preview list and stop refreshing animation
     */
    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.tradewind)
        swipeRefreshLayout.setOnRefreshListener {
            // TODO add real refreshing/success/error state to requests
            viewModel.updatePlaces()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupFloatingActionButton() =
        floatingActionButton.setOnClickListener {
            requireFragmentManager().createAndReplaceFragment(
                FragmentTag.CREATE_PLACE_FRAGMENT,
                CreatePlaceFragment::class,
                addToBackStack = true
            )
        }
}