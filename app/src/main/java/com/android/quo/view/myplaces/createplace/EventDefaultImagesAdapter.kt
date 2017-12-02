package com.android.quo.view.myplaces.createplace

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_default_image.defaultImageView

/**
 * Created by Jung on 02.12.17.
 */

class EventDefaultImagesAdapter(private val list: ArrayList<Drawable>) :
        RecyclerView.Adapter<EventDefaultImagesAdapter.EventDefaultImagesViewHolder>() {

    override fun onBindViewHolder(holder: EventDefaultImagesViewHolder, position: Int) {
        holder.bindItem(list, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventDefaultImagesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.event_default_image, parent, false)
        return EventDefaultImagesViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    class EventDefaultImagesViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindItem(list: ArrayList<Drawable>, position: Int) {
            defaultImageView.background = list[position]
        }
    }
}