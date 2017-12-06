package com.android.quo.view.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.android.quo.QuoApplication
import com.android.quo.R
import com.android.quo.networking.ApiService
import com.android.quo.networking.PlaceRepository
import com.android.quo.view.PlacePreviewAdapter
import com.android.quo.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.placePreviewRecyclerView
import kotlinx.android.synthetic.main.fragment_home.swipeRefreshLayout

/**
 * Created by Jung on 01.11.17.
 */
class HomeFragment : Fragment() {

    private val placeDao = QuoApplication.database.placeDao()
    private val apiService = ApiService.instance
    private val placeRepository = PlaceRepository(placeDao, apiService)

    private val viewModel = HomeViewModel(placeRepository)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.bottomNavigationView?.visibility = VISIBLE

        observePlacePreviewList()
        setupSwipeRefresh()
    }

    /**
     * Observe place preview list and set adapter for place preview recycler view
     */
    private fun observePlacePreviewList() {
        viewModel.getPlaces().observe(this, Observer {
            it?.let { list ->
                activity?.let { activity ->
                    placePreviewRecyclerView.adapter = PlacePreviewAdapter(list, activity.supportFragmentManager)
                    placePreviewRecyclerView.layoutManager = LinearLayoutManager(this.context)
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
            //            placePreviewListViewModel.updatePlacePreviewList(HOME)
            swipeRefreshLayout.isRefreshing = false
        }
    }
}