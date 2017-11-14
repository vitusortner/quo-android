package com.android.quo.view.place

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import kotlinx.android.synthetic.main.gallery_image.view.imageView

/**
 * Created by vitusortner on 14.11.17.
 */
class PlaceGalleryAdapter : RecyclerView.Adapter<PlaceGalleryAdapter.PlaceGalleryViewHolder>() {

    override fun getItemCount() = 40

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): PlaceGalleryAdapter.PlaceGalleryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_image, parent, false)
        return PlaceGalleryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaceGalleryViewHolder, position: Int) {
        holder.bindItem(position)
    }

    class PlaceGalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(position: Int) {
            val imageUrl = when {
                (position % 2 == 0) -> "https://static.pexels.com/photos/196643/pexels-photo-196643.jpeg"
                (position % 3 == 0) -> "https://static.pexels.com/photos/226183/pexels-photo-226183.jpeg"
                else -> "https://static.pexels.com/photos/196652/pexels-photo-196652.jpeg"
            }

            Glide.with(itemView.context)
                    .load(imageUrl)
                    .apply(centerCropTransform())
                    .into(itemView.imageView)
        }
    }
}