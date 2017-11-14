package com.android.quo.view.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.view.PlacePreviewAdapter
import com.android.quo.viewmodel.PlacePreviewListViewModel
import com.android.quo.viewmodel.PlacePreviewListViewModel.FragmentType.HOME
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.placePreviewRecyclerView
import kotlinx.android.synthetic.main.fragment_home.swipeRefreshLayout

/**
 * Created by Jung on 01.11.17.
 */
class HomeFragment : Fragment() {

    private lateinit var placePreviewListViewModel: PlacePreviewListViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.bottomNavigationView.visibility = VISIBLE

        placePreviewListViewModel = ViewModelProviders.of(this)
                .get(PlacePreviewListViewModel().javaClass)

        observePlacePreviewList()
        setupSwipeRefresh()
    }

    /**
     * Observe place preview list and set adapter for place preview recycler view
     */
    private fun observePlacePreviewList() =
            placePreviewListViewModel.getPlacePreviewList(HOME).observe(this, Observer { list ->
                list?.let {
                    placePreviewRecyclerView.adapter = PlacePreviewAdapter(this.context, list, activity)
                    placePreviewRecyclerView.layoutManager = LinearLayoutManager(this.context)
                }
            })

    /**
     * Update place preview list and stop refreshing animation
     */
    private fun setupSwipeRefresh() =
            swipeRefreshLayout.setOnRefreshListener {
                placePreviewListViewModel.updatePlacePreviewList(HOME)
                swipeRefreshLayout.isRefreshing = false
            }
}