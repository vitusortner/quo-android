package com.android.quo.view.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.model.PlacePreview
import com.bumptech.glide.Glide
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.place_preview_card_view.placePreviewCardView
import kotlinx.android.synthetic.main.place_preview_card_view.placePreviewDescriptionTextView
import kotlinx.android.synthetic.main.place_preview_card_view.placePreviewImageView
import kotlinx.android.synthetic.main.place_preview_card_view.placePreviewTitleTextView

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewAdapter(private val context: Context, private val list: List<PlacePreview>) : RecyclerView.Adapter<PlacePreviewAdapter.PlacePreviewViewHolder>() {

    override fun onBindViewHolder(holder: PlacePreviewViewHolder, position: Int) = holder.bindItem(list, position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePreviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.place_preview_card_view, parent, false)
        return PlacePreviewViewHolder(context, itemView)
    }

    override fun getItemCount(): Int = list.size

    class PlacePreviewViewHolder(private val context: Context, override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindItem(list: List<PlacePreview>, position: Int) {
            val url = list[position].imageUrl

            Glide.with(context)
                .load(url)
                .listener(GlidePalette.with(url)
                    .use(BitmapPalette.Profile.MUTED)
                    .intoBackground(placePreviewCardView)
                )
                .into(placePreviewImageView)

            placePreviewTitleTextView.text = list[position].title
            placePreviewDescriptionTextView.text = list[position].description
        }
    }
}