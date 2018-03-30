package com.android.quo.view.place.page

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.quo.R
import com.android.quo.util.Constants.Extra
import com.android.quo.view.BaseFragment
import com.android.quo.viewmodel.PageViewModel
import kotlinx.android.synthetic.main.fragment_place_page.recyclerView
import kotlinx.android.synthetic.main.fragment_place_page.swipeRefreshLayout
import org.koin.android.architecture.ext.viewModel

/**
 * Created by vitusortner on 12.11.17.
 */
class PageFragment : BaseFragment(R.layout.fragment_place_page) {

    private val viewModel by viewModel<PageViewModel>(false)

    private lateinit var adapter: PageAdapter

    private var placeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = PageAdapter(imageLoader)
        placeId = arguments?.getString(Extra.PLACE_ID_EXTRA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeComponents()
        setupSwipeRefresh()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeComponents() =
        placeId?.let { placeId ->
            viewModel.getComponents(placeId)
                .observe(
                    this,
                    Observer { it?.let { adapter.setItems(it) } }
                )
        }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.tradewind)
        swipeRefreshLayout.setOnRefreshListener {
            placeId?.let {
                viewModel.updateComponents(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}