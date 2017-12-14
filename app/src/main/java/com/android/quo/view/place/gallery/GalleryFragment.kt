package com.android.quo.view.place.gallery

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.QuoApplication
import com.android.quo.R
import com.android.quo.networking.ApiService
import com.android.quo.networking.SyncService
import com.android.quo.networking.repository.PictureRepository
import com.android.quo.viewmodel.GalleryViewModel
import com.android.quo.viewmodel.factory.GalleryViewModelFactory
import kotlinx.android.synthetic.main.fragment_place_gallery.floatingActionButton
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

    private lateinit var viewModel: GalleryViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders
                .of(this, GalleryViewModelFactory(pictureRepository))
                .get(GalleryViewModel::class.java)

        activity?.let { activity ->
            viewModel.getPictures().observe(this, Observer {
                it?.let {
                    recyclerView.adapter = GalleryAdapter(activity, it)
                    recyclerView.layoutManager = GridLayoutManager(this.context, 3)
                }
            })
        }

        setupSwipeRefresh()

        floatingActionButton.setOnClickListener {
            context?.let { context ->
                val bottomSheetDialog = BottomSheetDialog(context)
                val layout = activity?.layoutInflater?.inflate(R.layout.bottom_sheet_add_image, null)
                layout?.let {
                    bottomSheetDialog.setContentView(it)
                    bottomSheetDialog.show()
                }
            }

        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.updatePictures()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}