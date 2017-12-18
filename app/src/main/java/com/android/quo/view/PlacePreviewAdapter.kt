package com.android.quo.view

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.view.place.PlaceFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.place_preview_cardview.pagePreviewCardView
import kotlinx.android.synthetic.main.place_preview_cardview.placePreviewDescriptionTextView
import kotlinx.android.synthetic.main.place_preview_cardview.placePreviewImageView
import kotlinx.android.synthetic.main.place_preview_cardview.placePreviewTitleTextView

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewAdapter(
        private val list: List<Place>,
        private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<PlacePreviewAdapter.PlacePreviewViewHolder>() {

    override fun onBindViewHolder(holder: PlacePreviewViewHolder, position: Int) =
            holder.bindItem(list, position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePreviewViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.place_preview_cardview, parent, false)
        return PlacePreviewViewHolder(itemView, fragmentManager)
    }

    override fun getItemCount(): Int = list.size

    class PlacePreviewViewHolder(
            override val containerView: View,
            private val fragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindItem(list: List<Place>, position: Int) {
            RxView.clicks(pagePreviewCardView)
                    .subscribe {
                        val bundle = Bundle()
                        bundle.putParcelable("place", list[position])
                        val fragment = PlaceFragment()
                        fragment.arguments = bundle

                        fragmentManager
                                .beginTransaction()
                                .add(R.id.content, fragment)
                                .addToBackStack(null)
                                .commit()
                    }

            val imageUrl = list[position].titlePicture

            Glide.with(containerView.context)
                    .load(imageUrl)
                    .apply(centerCropTransform())
                    .into(placePreviewImageView)

            placePreviewTitleTextView.text = list[position].title
            placePreviewDescriptionTextView.text = list[position].description
        }
    }
}