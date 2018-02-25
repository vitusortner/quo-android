package com.android.quo.view.place.gallery

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Picture
import com.android.quo.view.BaseRecyclerViewAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import kotlinx.android.synthetic.main.gallery_image_thumbnail.view.imageView

/**
 * Created by vitusortner on 14.11.17.
 */
class GalleryAdapter(
    private val imageLoader: RequestManager,
    private val onClick: (List<Picture>, position: Int) -> Unit
) :
    BaseRecyclerViewAdapter<Picture>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(inflateView(parent, R.layout.gallery_image_thumbnail))

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