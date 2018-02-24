package com.android.quo.view.home

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.util.Constants
import com.android.quo.util.extension.addFragment
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
class HomeFragment : Fragment() {

    private val viewModel by viewModel<HomeViewModel>(false)

    private lateinit var adapter: PlacePreviewAdapter

    companion object {

        fun onClick(place: Place, fragmentManager: FragmentManager?) {
            val bundle = Bundle()
            bundle.putParcelable(Constants.Extra.PLACE_EXTRA, place)
            val fragment = PlaceFragment()
            fragment.arguments = bundle

            fragmentManager?.addFragment(
                fragment,
                true,
                R.anim.slide_in to R.anim.slide_out
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.bottomNavigationView?.visibility = VISIBLE

        adapter = PlacePreviewAdapter { onClick(it, fragmentManager) }
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

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

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
                    viewModel.logout()
                    val intent = Intent(this.context, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    /**
     * Observe place preview list and set adapter for place preview recycler view
     */
    private fun observePlaces() =
        viewModel.getPlaces().observe(
            this,
            Observer { it?.let { places -> adapter.setItems(places) } })

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