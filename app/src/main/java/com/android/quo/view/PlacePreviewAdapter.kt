package com.android.quo.view

import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.model.PlacePreview
import com.android.quo.view.place.PlaceFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.place_preview_cardview.placePreviewCardView
import kotlinx.android.synthetic.main.place_preview_cardview.placePreviewDescriptionTextView
import kotlinx.android.synthetic.main.place_preview_cardview.placePreviewImageView
import kotlinx.android.synthetic.main.place_preview_cardview.placePreviewTitleTextView

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewAdapter(
        private val list: List<PlacePreview>,
        private val supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<PlacePreviewAdapter.PlacePreviewViewHolder>() {

    override fun onBindViewHolder(holder: PlacePreviewViewHolder, position: Int) =
            holder.bindItem(list, position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePreviewViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.place_preview_cardview, parent, false)
        return PlacePreviewViewHolder(itemView, supportFragmentManager)
    }

    override fun getItemCount(): Int = list.size

    class PlacePreviewViewHolder(
            override val containerView: View,
            private val supportFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindItem(list: List<PlacePreview>, position: Int) {
            RxView.clicks(placePreviewCardView)
                    .subscribe {
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.content, PlaceFragment())
                                .addToBackStack(null)
                                .commit()
                    }

            val imageUrl = list[position].imageUrl

            Glide.with(containerView.context)
                    .load(imageUrl)
                    .apply(centerCropTransform())
                    // Set card view background color with Palette
                    .listener(GlidePalette.with(imageUrl)
                            .intoCallBack { palette ->
                                palette?.let {
                                    placePreviewCardView.setCardBackgroundColor(
                                            palette.getMutedColor(BitmapPalette.Profile.MUTED))
                                }
                            }
                    )
                    .into(placePreviewImageView)

            placePreviewTitleTextView.text = list[position].title
            placePreviewDescriptionTextView.text = list[position].description
        }
    }
}