package com.android.quo.view.place.page

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Component
import com.android.quo.view.place.page.PageAdapter.ViewType.PICTURE
import com.android.quo.view.place.page.PageAdapter.ViewType.TEXT
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.component_image.imageView
import kotlinx.android.synthetic.main.component_text.textView

/**
 * Created by vitusortner on 11.12.17.
 */
class PageAdapter(private val list: List<Component>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType(val value: Int) {
        PICTURE(1),
        TEXT(2)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            list[position].picture !== null -> PICTURE.value
            list[position].text !== null -> TEXT.value
            else -> 0
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                val imageUrl = list[position].picture

                Glide.with(holder.containerView.context)
                        .load(imageUrl)
                        .into(holder.imageView)
            }
            is TextViewHolder -> {
                holder.textView.text = list[position].text
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            PICTURE.value -> {
                val itemView = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.component_image, parent, false)
                ImageViewHolder(itemView)
            }
            TEXT.value -> {
                val itemView = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.component_text, parent, false)
                TextViewHolder(itemView)
            }
            else -> null
        }
    }

    class TextViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer

    class ImageViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer
}