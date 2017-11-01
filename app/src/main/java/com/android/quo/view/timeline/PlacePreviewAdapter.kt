package com.android.quo.view.timeline

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.model.PlacePreview
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.place_preview_card_view.placePreviewDescriptionTextView
import kotlinx.android.synthetic.main.place_preview_card_view.placePreviewTitleTextView

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewAdapter(private val list: List<PlacePreview>) : RecyclerView.Adapter<PlacePreviewAdapter.PlacePreviewViewHolder>() {

    override fun onBindViewHolder(holder: PlacePreviewViewHolder, position: Int) = holder.bindItem(list, position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePreviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.place_preview_card_view, parent, false)
        return PlacePreviewViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    class PlacePreviewViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindItem(list: List<PlacePreview>, position: Int) {
            Log.i("Debugging", "title: ${list[position].title} description: ${list[position].description} at position: $position")

            placePreviewTitleTextView.text = list[position].title
            placePreviewDescriptionTextView.text = list[position].description
        }
    }
}