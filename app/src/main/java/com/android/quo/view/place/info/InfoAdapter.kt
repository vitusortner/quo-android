package com.android.quo.view.place.info

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.view.place.info.ViewType.ADDRESS
import com.android.quo.view.place.info.ViewType.DESCRIPTION
import com.android.quo.view.place.info.ViewType.TIME
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.place_info_address_cardview.addressNameTextView
import kotlinx.android.synthetic.main.place_info_address_cardview.addressTextView
import kotlinx.android.synthetic.main.place_info_description_cardview.contentTextView
import kotlinx.android.synthetic.main.place_info_time_cardview.dateTextView

/**
 * Created by vitusortner on 22.11.17.
 */
private enum class ViewType(val value: Int) {
    DESCRIPTION(1),
    ADDRESS(2),
    TIME(3)
}

class InfoAdapter(private val place: Place) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> DESCRIPTION.value
            1 -> ADDRESS.value
            2 -> TIME.value
            else -> 0
        }
    }

    override fun getItemCount(): Int {
        return when {
            place.address == null && place.endDate == null -> 1
            place.address == null || place.endDate == null -> 2
            else -> 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            DESCRIPTION.value -> {
                val descriptionCardView = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.place_info_description_cardview, parent, false)
                DescriptionViewHolder(descriptionCardView)
            }
            ADDRESS.value -> {
                val addressCardView = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.place_info_address_cardview, parent, false)
                AddressViewHolder(addressCardView)
            }
            TIME.value -> {
                val timeCardView = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.place_info_time_cardview, parent, false)
                TimeViewHolder(timeCardView)
            }
            else -> null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is DescriptionViewHolder -> holder.contentTextView.text = place.description
            is AddressViewHolder -> {
                // TODO place name in address class
                holder.addressNameTextView.text = "TODO Place name"
                holder.addressTextView.text = getAddress()
            }
            is TimeViewHolder -> holder.dateTextView.text = place.endDate
        }
    }

    private fun getAddress(): String {
        place.address?.let {
            return "${it.street}, ${it.zipCode} ${it.city}"
        } ?: return ""
    }

    class DescriptionViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer

    class AddressViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer

    class TimeViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer
}