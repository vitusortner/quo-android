package com.android.quo.view.home

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.view.BaseRecyclerViewAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import kotlinx.android.synthetic.main.place_preview_cardview.view.pagePreviewCardView
import kotlinx.android.synthetic.main.place_preview_cardview.view.placePreviewDescriptionTextView
import kotlinx.android.synthetic.main.place_preview_cardview.view.placePreviewImageView
import kotlinx.android.synthetic.main.place_preview_cardview.view.placePreviewTitleTextView

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewAdapter(
    private val imageLoader: RequestManager,
    private val onClick: (Place) -> Unit
) :
    BaseRecyclerViewAdapter<Place>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(inflateView(parent, R.layout.place_preview_cardview))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            pagePreviewCardView.setOnClickListener { onClick(list[position]) }

            placePreviewTitleTextView.text = list[position].title
            placePreviewDescriptionTextView.text = list[position].description

            val imageUrl = list[position].titlePicture
            imageLoader
                .load(imageUrl)
                .apply(centerCropTransform())
                .into(placePreviewImageView)
        }
    }
}