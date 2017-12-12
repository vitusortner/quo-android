package com.android.quo.view.place.page

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.QuoApplication
import com.android.quo.R
import com.android.quo.networking.ApiService
import com.android.quo.networking.SyncService
import com.android.quo.networking.repository.ComponentRepository
import com.android.quo.viewmodel.PageViewModel
import com.android.quo.viewmodel.factory.PageViewModelFactory
import kotlinx.android.synthetic.main.fragment_place_page.recyclerView
import kotlinx.android.synthetic.main.fragment_place_page.swipeRefreshLayout

/**
 * Created by vitusortner on 12.11.17.
 */
class PageFragment : Fragment() {

    private val database = QuoApplication.database
    private val componentDao = database.componentDao()
    private val apiService = ApiService.instance
    private val syncService = SyncService(database)
    private val componentRepository = ComponentRepository(componentDao, apiService, syncService)

    private lateinit var viewModel: PageViewModel

    private var placeId: String? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        placeId = arguments?.getString("placeId")
        Log.i("place id", placeId ?: "")

        return inflater.inflate(R.layout.fragment_place_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders
                .of(this, PageViewModelFactory(componentRepository))
                .get(PageViewModel::class.java)

        observeComponents()
        setupSwipeRefresh()
    }

    private fun observeComponents() {
        // TODO show components of place
        viewModel.getComponents().observe(this, Observer {
            it?.let {
                recyclerView.adapter = PageAdapter(it)
                recyclerView.layoutManager = LinearLayoutManager(this.context)
            }
        })
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.updateComponents()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}