package com.android.quo.view.place.gallery

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Picture
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.gallery_image_thumbnail.imageView

/**
 * Created by vitusortner on 14.11.17.
 */
class GalleryAdapter(private val activity: Activity, private val list: List<Picture>) :
        RecyclerView.Adapter<GalleryAdapter.PlaceGalleryViewHolder>() {

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceGalleryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_image_thumbnail, parent, false)
        return PlaceGalleryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaceGalleryViewHolder, position: Int) {
        val imageUrl = list[position].src

        Glide.with(holder.containerView.context)
                .load(imageUrl)
                .apply(centerCropTransform())
                .into(holder.imageView)

        RxView.clicks(holder.imageView)
                .subscribe {
                    val intent = Intent(activity, ImagePagerActivity::class.java)
                    intent.putParcelableArrayListExtra("list", ArrayList(list))
                    intent.putExtra("position", position)
                    holder.containerView.context.startActivity(intent)
                }
    }

    class PlaceGalleryViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer
}