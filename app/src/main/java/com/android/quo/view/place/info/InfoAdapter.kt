package com.android.quo.view.place.info

import android.annotation.SuppressLint
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.util.Constants.Date.MONGO_DB_TIMESTAMP_FORMAT
import com.android.quo.util.extension.toDate
import com.bumptech.glide.RequestManager
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
class InfoAdapter(private val imageLoader: RequestManager, private val place: Place) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DESCRIPTION = 1
    private val ADDRESS = 2
    private val TIME = 3
    private val QR_CODE = 4

    override fun getItemCount(): Int {
        var items = when {
            place.address == null && place.endDate == null -> 1
            place.address == null || place.endDate == null -> 2
            else -> 3
        }
        if (place.isHost) items += 1
        return items
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> DESCRIPTION
            1 -> {
                if (place.address == null && place.endDate == null) {
                    QR_CODE
                } else if (place.address == null) {
                    TIME
                } else {
                    ADDRESS
                }
            }
            2 -> {
                if (place.endDate == null) QR_CODE else TIME
            }
            3 -> QR_CODE
            else -> 0
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            DESCRIPTION -> DescriptionViewHolder(
                inflateView(parent, R.layout.place_info_description_cardview)
            )
            ADDRESS -> AddressViewHolder(inflateView(parent, R.layout.place_info_address_cardview))
            TIME -> TimeViewHolder(inflateView(parent, R.layout.place_info_time_cardview))
            QR_CODE -> QrCodeViewHolder(inflateView(parent, R.layout.place_info_qr_code_cardview))
            else -> DescriptionViewHolder(
                inflateView(parent, R.layout.place_info_description_cardview)
            )
        }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DescriptionViewHolder -> holder.contentTextView.text = place.description
            is AddressViewHolder -> {
                place.address?.let {
                    holder.addressNameTextView.text = it.name
                    holder.addressTextView.text = "${it.street}, ${it.zipCode} ${it.city}"
                }
            }
            is TimeViewHolder -> holder.dateTextView.text = formatDate(place.endDate) ?: ""
            is QrCodeViewHolder -> {
                imageLoader
                    .load(place.qrCode)
                    .into(holder.imageView)
            }
        }
    }

    fun inflateView(parent: ViewGroup, @LayoutRes layout: Int) =
        LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(dateString: String?): String? =
        dateString?.toDate(MONGO_DB_TIMESTAMP_FORMAT)?.let { SimpleDateFormat("dd.MM.yyyy").format(it) }

    private class DescriptionViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    private class AddressViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    private class TimeViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    private class QrCodeViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer
}