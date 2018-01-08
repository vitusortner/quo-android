package com.android.quo.view.createplace

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.quo.R
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_default_image.defaultImageView

/**
 * Created by Jung on 02.12.17.
 */

class EventDefaultImagesAdapter(private val list: List<Drawable>, private val headerImageView: ImageView) :
        RecyclerView.Adapter<EventDefaultImagesAdapter.EventDefaultImagesViewHolder>() {

    override fun onBindViewHolder(holder: EventDefaultImagesViewHolder, position: Int) {
        holder.bindItem(list, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventDefaultImagesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.event_default_image, parent, false)
        return EventDefaultImagesViewHolder(itemView, headerImageView)
    }

    override fun getItemCount(): Int = list.size

    class EventDefaultImagesViewHolder(
            override val containerView: View,
            private val headerImageView: ImageView
    ) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindItem(list: List<Drawable>, position: Int) {
            RxView.clicks(defaultImageView)
                    .subscribe {
                        CreatePlace.place.titlePicture = "quo_default_${adapterPosition + 1}.png"
                        headerImageView.setImageDrawable(list[adapterPosition])
                    }
            defaultImageView.setImageDrawable(list[position])
        }
    }
}