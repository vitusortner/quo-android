package com.android.quo.view.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.android.quo.Application
import com.android.quo.R
import com.android.quo.view.PlacePreviewAdapter
import com.android.quo.view.login.LoginActivity
import com.android.quo.viewmodel.HomeViewModel
import com.android.quo.viewmodel.factory.HomeViewModelFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.recyclerView
import kotlinx.android.synthetic.main.fragment_home.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_home.toolbar

/**
 * Created by Jung on 01.11.17.
 */
class HomeFragment : Fragment() {

    private val placeRepository = Application.placeRepository
    private val userRepository = Application.userRepository

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.bottomNavigationView?.visibility = VISIBLE

        viewModel = ViewModelProviders
                .of(this, HomeViewModelFactory(placeRepository, userRepository))
                .get(HomeViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observePlaces()
        setupSwipeRefresh()
    }

    override fun onResume() {
        super.onResume()

        viewModel.updatePlaces()
    }

    private fun setupToolbar() {
        toolbar.inflateMenu(R.menu.home_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.title) {
                resources.getString(R.string.menu_settings) -> {
                }
                resources.getString(R.string.menu_help) -> {
                }
                resources.getString(R.string.menu_info) -> {
                }
                resources.getString(R.string.menu_logout) -> {
                    logoutUserObservable()
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                }
            }

            true
        }
    }

    private fun logoutUserObservable(): Observable<Unit> {
        return Observable.create {
            Application.database.userDao().deleteAllUsers()
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }
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
}