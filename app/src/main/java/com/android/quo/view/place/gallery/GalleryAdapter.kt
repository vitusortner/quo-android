package com.android.quo.view.place.gallery

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.view.BaseRecyclerViewAdapter
import com.android.quo.viewmodel.GalleryPicture
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import kotlinx.android.synthetic.main.layout_gallery_image_thumbnail.view.*

/**
 * Created by vitusortner on 14.11.17.
 */
class GalleryAdapter(
    private val imageLoader: RequestManager,
    private val onClick: (List<GalleryPicture>, position: Int) -> Unit
) :
    BaseRecyclerViewAdapter<GalleryPicture>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(inflateView(parent, R.layout.layout_gallery_image_thumbnail))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            val imageUrl = list[position].src
            imageLoader
                .load(imageUrl)
                .apply(centerCropTransform())
                .into(imageView)

            imageView.setOnClickListener { onClick(list, position) }
        }
    }
}