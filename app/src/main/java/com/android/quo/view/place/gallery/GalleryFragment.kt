package com.android.quo.view.place.gallery

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.android.quo.R
import com.android.quo.db.entity.Picture
import com.android.quo.util.Constants
import com.android.quo.util.Constants.Extra
import com.android.quo.view.BaseFragment
import com.android.quo.viewmodel.GalleryViewModel
import kotlinx.android.synthetic.main.fragment_place_gallery.recyclerView
import kotlinx.android.synthetic.main.fragment_place_gallery.swipeRefreshLayout
import org.koin.android.architecture.ext.viewModel

/**
 * Created by vitusortner on 14.11.17.
 */
class GalleryFragment : BaseFragment(R.layout.fragment_place_gallery) {

    private val viewModel by viewModel<GalleryViewModel>(false)

    private lateinit var adapter: GalleryAdapter

    private var placeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placeId = arguments?.getString(Extra.PLACE_ID_EXTRA)
        adapter = GalleryAdapter(imageLoader) { list, position -> onClick(list, position) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, Constants.GALLERY_COLUMN_COUNT)

        observePictures()
        setupSwipeRefresh()
    }

    private fun observePictures() {
        placeId?.let { placeId ->
            viewModel.getPictures(placeId)
                .observe(
                    this,
                    Observer { it?.let { adapter.setItems(it) } }
                )
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.tradewind)
        swipeRefreshLayout.setOnRefreshListener {
            placeId?.let { viewModel.updatePictures(it) }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun onClick(list: List<Picture>, position: Int) {
        val intent = Intent(context, ImagePagerActivity::class.java)
        intent.putParcelableArrayListExtra(Extra.PICTURE_LIST_EXTRA, ArrayList(list))
        intent.putExtra(Extra.PICTURE_POSITION_EXTRA, position)
        requireContext().startActivity(intent)
    }
}