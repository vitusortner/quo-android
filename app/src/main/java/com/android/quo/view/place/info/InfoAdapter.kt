package com.android.quo.view.place.info

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.util.extension.toDate
import com.android.quo.view.place.info.ViewType.ADDRESS
import com.android.quo.view.place.info.ViewType.DESCRIPTION
import com.android.quo.view.place.info.ViewType.QR_CODE
import com.android.quo.view.place.info.ViewType.TIME
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.place_info_address_cardview.addressNameTextView
import kotlinx.android.synthetic.main.place_info_address_cardview.addressTextView
import kotlinx.android.synthetic.main.place_info_description_cardview.contentTextView
import kotlinx.android.synthetic.main.place_info_qr_code_cardview.imageView
import kotlinx.android.synthetic.main.place_info_time_cardview.dateTextView
import java.text.SimpleDateFormat

/**
 * Created by vitusortner on 22.11.17.
 */
private enum class ViewType(val value: Int) {
    DESCRIPTION(1),
    ADDRESS(2),
    TIME(3),
    QR_CODE(4)
}

class InfoAdapter(private val place: Place) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        var items = when {
            place.address == null && place.endDate == null -> 1
            place.address == null || place.endDate == null -> 2
            else -> 3
        }
        if (place.isHost) {
            items += 1
        }
        return items
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> DESCRIPTION.value
            1 -> {
                return if (place.address == null && place.endDate == null) {
                    QR_CODE.value
                } else if (place.address == null) {
                    TIME.value
                } else {
                    ADDRESS.value
                }
            }
            2 -> {
                return if (place.endDate == null) {
                    QR_CODE.value
                } else {
                    TIME.value
                }
            }
            3 -> QR_CODE.value
            else -> 0
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
            QR_CODE.value -> {
                val qrCodeCardView = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.place_info_qr_code_cardview, parent, false)
                QrCodeViewHolder(qrCodeCardView)
            }
            else -> null
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is DescriptionViewHolder -> holder.contentTextView.text = place.description
            is AddressViewHolder -> {
                place.address?.let {
                    holder.addressNameTextView.text = it.name
                    holder.addressTextView.text = "${it.street}, ${it.zipCode} ${it.city}"
                }
            }
            is TimeViewHolder -> holder.dateTextView.text = formatDate(place.endDate)
            is QrCodeViewHolder -> {
                Glide.with(holder.containerView.context)
                        .load(place.qrCode)
                        .into(holder.imageView)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(dateString: String?): String {
        dateString.toDate()?.let {
            return SimpleDateFormat("dd.MM.yyyy").format(it)
        } ?: run {
            return ""
        }
    }

    class DescriptionViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer

    class AddressViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer

    class TimeViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer

    class QrCodeViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer
}