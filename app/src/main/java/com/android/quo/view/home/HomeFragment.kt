package com.android.quo.view.home


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.viewmodel.PlacePreviewListViewModel
import kotlinx.android.synthetic.main.fragment_home.homeSwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_home.placePreviewRecyclerView


/**
 * Created by Jung on 01.11.17.
 */

class HomeFragment : Fragment() {

    private lateinit var placePreviewListViewModel: PlacePreviewListViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placePreviewListViewModel = ViewModelProviders.of(this)
                .get(PlacePreviewListViewModel().javaClass)

        observePlacePreviewList()
        setupSwipeRefresh()
    }

    /**
     * Observe place preview list and set adapter for place preview recycler view
     */
    private fun observePlacePreviewList() {
        placePreviewListViewModel.getPlacePreviewList().observe(this, Observer { list ->
            list?.let {
                placePreviewRecyclerView.adapter = PlacePreviewAdapter(this.context, list)
                placePreviewRecyclerView.layoutManager = LinearLayoutManager(this.context)
            }
        })
    }

    /**
     * Update place preview list and stop refreshing animation
     */
    private fun setupSwipeRefresh() {
        homeSwipeRefreshLayout.setOnRefreshListener {
            placePreviewListViewModel.updatePlacePreviewList()
            homeSwipeRefreshLayout.isRefreshing = false
        }
    }
}