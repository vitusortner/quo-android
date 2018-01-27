package com.android.quo.view.place.gallery

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.di.Injection
import com.android.quo.viewmodel.GalleryViewModel
import com.android.quo.viewmodel.factory.GalleryViewModelFactory
import kotlinx.android.synthetic.main.fragment_place_gallery.recyclerView
import kotlinx.android.synthetic.main.fragment_place_gallery.swipeRefreshLayout

/**
 * Created by vitusortner on 14.11.17.
 */
class GalleryFragment : Fragment() {

    private var placeId: String? = null

    private lateinit var adapter: GalleryAdapter

    private lateinit var viewModel: GalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        placeId = arguments?.getString("placeId")

        activity?.let {
            adapter = GalleryAdapter(it)
        }

        viewModel = ViewModelProviders
                .of(this, GalleryViewModelFactory(Injection.pictureRepository))
                .get(GalleryViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        observerPictures()
        setupSwipeRefresh()
    }

    private fun observerPictures() {
        placeId?.let { placeId ->
            viewModel.getPictures(placeId).observe(this, Observer {
                it?.let {
                    adapter.setItems(it)
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            placeId?.let {
                viewModel.updatePictures(it)
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}