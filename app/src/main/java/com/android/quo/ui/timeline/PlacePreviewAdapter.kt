package com.android.quo.ui.timeline

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.viewmodel.PlacePreviewViewModel
import kotlinx.android.synthetic.main.place_preview_card_view.view.placeDescriptionTextView
import kotlinx.android.synthetic.main.place_preview_card_view.view.placePreviewTitleTextView

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewAdapter(val viewModel: PlacePreviewViewModel) : RecyclerView.Adapter<PlacePreviewAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(viewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.place_preview_card_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(placePreview: PlacePreviewViewModel) {
            itemView.placePreviewTitleTextView.text = placePreview.title
            itemView.placeDescriptionTextView.text = placePreview.description
        }
    }
}