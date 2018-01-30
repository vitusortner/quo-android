package com.android.quo.view.place.gallery

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Picture
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.gallery_image_thumbnail.imageView

/**
 * Created by vitusortner on 14.11.17.
 */
class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.PlaceGalleryViewHolder>() {

    private var list = emptyList<Picture>()

    fun setItems(pictures: List<Picture>) {
        list = pictures
        notifyDataSetChanged()
    }

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

        holder.imageView.setOnClickListener {
            val intent = Intent(holder.containerView.context, ImagePagerActivity::class.java)
            intent.putParcelableArrayListExtra("list", ArrayList(list))
            intent.putExtra("position", position)
            holder.containerView.context.startActivity(intent)
        }
    }

    class PlaceGalleryViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer
}