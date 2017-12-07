package com.android.quo.view.place.gallery

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.QuoApplication
import com.android.quo.R
import com.android.quo.networking.ApiService
import com.android.quo.networking.PictureRepository
import com.android.quo.networking.SyncService
import com.android.quo.viewmodel.PlaceViewModel
import com.android.quo.viewmodel.factory.PlaceViewModelFactory
import kotlinx.android.synthetic.main.fragment_place_gallery.recyclerView
import kotlinx.android.synthetic.main.fragment_place_gallery.swipeRefreshLayout

/**
 * Created by vitusortner on 14.11.17.
 */
class GalleryFragment : Fragment() {

    private val database = QuoApplication.database
    private val pictureDao = database.pictureDao()
    private val apiService = ApiService.instance
    private val syncService = SyncService(database)
    private val pictureRepository = PictureRepository(pictureDao, apiService, syncService)

    private lateinit var viewModel: PlaceViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO real viewmodel data handling (LiveData)
        this.parentFragment?.let { parentFragment ->
            viewModel = ViewModelProviders
                    .of(parentFragment, PlaceViewModelFactory(pictureRepository))
                    .get(PlaceViewModel::class.java)

            activity?.let { activity ->
                viewModel.getPictures().observe(this, Observer {
                    it?.let {
                        recyclerView.adapter = GalleryAdapter(activity, it)
                        recyclerView.layoutManager = GridLayoutManager(this.context, 3)
                    }
                })
            }
        }

        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            // TODO updata data
            swipeRefreshLayout.isRefreshing = false
        }
    }
}