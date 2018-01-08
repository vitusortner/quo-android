package com.android.quo.view.myplaces

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.Application
import com.android.quo.R
import com.android.quo.network.service.ApiService
import com.android.quo.network.service.SyncService
import com.android.quo.network.repository.PlaceRepository
import com.android.quo.view.PlacePreviewAdapter
import com.android.quo.view.myplaces.createplace.CreatePlaceFragment
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

    private val database = Application.database
    private val placeDao = database.placeDao()
    private val userDao = database.userDao()
    private val apiService = ApiService.instance
    private val syncService = SyncService(database)
    private val placeRepository = PlaceRepository(placeDao, apiService, syncService)

    private lateinit var viewModel: MyPlacesViewModel

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

        viewModel = ViewModelProviders
                .of(this, MyPlacesViewModelFactory(placeRepository, userDao))
                .get(MyPlacesViewModel::class.java)

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