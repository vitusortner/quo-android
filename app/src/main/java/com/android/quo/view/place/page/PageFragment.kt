package com.android.quo.view.place.page

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import kotlinx.android.synthetic.main.fragment_place_page.contentTextView
import kotlinx.android.synthetic.main.fragment_place_page.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_place_page.titleTextView

/**
 * Created by vitusortner on 12.11.17.
 */
class PageFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO viewmodel data
        titleTextView.text = "Rules"
        contentTextView.text = "1 Go Crazy\n2 No Rules"

        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            // TODO reload data
            swipeRefreshLayout.isRefreshing = false
        }
    }
}