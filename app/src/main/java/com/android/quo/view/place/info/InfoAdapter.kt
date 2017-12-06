package com.android.quo.view.place.info

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.place_info_cardview.contentTextView
import kotlinx.android.synthetic.main.place_info_cardview.titleTextView

/**
 * Created by vitusortner on 22.11.17.
 */
class InfoAdapter : RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

    override fun getItemCount() = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoAdapter.InfoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.place_info_cardview, parent, false)
        return InfoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InfoAdapter.InfoViewHolder, position: Int) {
        // TODO show real data
        holder.titleTextView.text = "Description"
        holder.contentTextView.text = "Want to be an astronaut or Lena Gerkes boyfriend? \n" +
                "Stan can help, let’s make it happen."
    }

    class InfoViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer
}