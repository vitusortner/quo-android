package com.android.quo.view.place.page

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.viewmodel.PageViewModel
import kotlinx.android.synthetic.main.fragment_place_page.recyclerView
import kotlinx.android.synthetic.main.fragment_place_page.swipeRefreshLayout
import org.koin.android.architecture.ext.getViewModel

/**
 * Created by vitusortner on 12.11.17.
 */
class PageFragment : Fragment() {

    private lateinit var viewModel: PageViewModel

    private var placeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        placeId = arguments?.getString("placeId")

        return inflater.inflate(R.layout.fragment_place_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeComponents()
        setupSwipeRefresh()
    }

    private fun observeComponents() {
        placeId?.let { placeId ->
            viewModel.getComponents(placeId).observe(this, Observer { components ->
                components?.let { components ->
                    recyclerView.adapter = PageAdapter(components)
                    recyclerView.layoutManager = LinearLayoutManager(this.context)
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            placeId?.let {
                viewModel.updateComponents(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}