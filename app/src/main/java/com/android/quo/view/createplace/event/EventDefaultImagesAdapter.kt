package com.android.quo.view.createplace.event

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.view.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.layout_event_default_image.view.defaultImageView

/**
 * Created by Jung on 02.12.17.
 */

class EventDefaultImagesAdapter(private val onClick: (drawable: Drawable, position: Int) -> Unit) :
    BaseRecyclerViewAdapter<Drawable>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            defaultImageView.setOnClickListener { onClick(list[position], position) }
            defaultImageView.setImageDrawable(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(inflateView(parent, R.layout.layout_event_default_image))
}