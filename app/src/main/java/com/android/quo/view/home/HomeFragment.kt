package com.android.quo.view.home

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.View.VISIBLE
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.util.Constants
import com.android.quo.util.extension.addFragment
import com.android.quo.util.extension.filterNull
import com.android.quo.util.extension.observeWithLifecycle
import com.android.quo.view.BaseFragment
import com.android.quo.view.login.LoginActivity
import com.android.quo.view.place.PlaceFragment
import com.android.quo.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.recyclerView
import kotlinx.android.synthetic.main.fragment_home.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_home.toolbar
import org.koin.android.architecture.ext.viewModel

/**
 * Created by Jung on 01.11.17.
 */
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var adapter: PlacePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().bottomNavigationView.visibility = VISIBLE

        adapter = PlacePreviewAdapter(imageLoader) { onClick(it, requireFragmentManager()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        setupToolbar()
        observe()
        setupSwipeRefresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePlaces()
    }

    private fun setupToolbar() {
        toolbar.inflateMenu(R.menu.menu_home)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.settings -> {
                }
                R.id.help -> {
                }
                R.id.info -> {
                }
                R.id.logout -> {
                    viewModel.logout()
                    Intent(context, LoginActivity::class.java).let { startActivity(it) }
                }
            }
            true
        }
    }

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
            viewModel.updatePlaces()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    companion object {

        fun onClick(place: Place, fragmentManager: FragmentManager) {
            val fragment = PlaceFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.Extra.PLACE_EXTRA, place)
                }
            }
            fragmentManager.addFragment(
                fragment,
                true,
                R.anim.slide_in to R.anim.slide_out
            )
        }
    }

}