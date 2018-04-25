package com.android.quo.view.home

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.view.BaseRecyclerViewAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import kotlinx.android.synthetic.main.cardview_place_preview.view.pagePreviewCardView
import kotlinx.android.synthetic.main.cardview_place_preview.view.placePreviewDescriptionTextView
import kotlinx.android.synthetic.main.cardview_place_preview.view.placePreviewImageView
import kotlinx.android.synthetic.main.cardview_place_preview.view.placePreviewTitleTextView

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewAdapter(
    private val imageLoader: RequestManager,
    private val onClick: (Place) -> Unit
) :
    BaseRecyclerViewAdapter<Place>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(inflateView(parent, R.layout.cardview_place_preview))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            list[position].apply {
                pagePreviewCardView.setOnClickListener { onClick(this) }
                placePreviewTitleTextView.text = title
                placePreviewDescriptionTextView.text = description
                imageLoader
                    .load(titlePicture)
                    .apply(centerCropTransform())
                    .into(placePreviewImageView)
            }
        }
    }
}