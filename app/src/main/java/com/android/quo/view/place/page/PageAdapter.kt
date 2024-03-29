package com.android.quo.view.place.page

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.view.BaseRecyclerViewAdapter
import com.android.quo.viewmodel.PageComponent
import com.bumptech.glide.RequestManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_component_image.*
import kotlinx.android.synthetic.main.layout_component_text.*

/**
 * Created by vitusortner on 11.12.17.
 */
class PageAdapter(private val imageLoader: RequestManager) :
    BaseRecyclerViewAdapter<PageComponent>() {

    private val PICTURE = 1
    private val TEXT = 2

    override fun getItemViewType(position: Int) =
        when {
            list[position].picture !== null -> PICTURE
            list[position].text !== null -> TEXT
            else -> 0
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            PICTURE -> ImageViewHolder(inflateView(parent, R.layout.layout_component_image))
            TEXT -> TextViewHolder(inflateView(parent, R.layout.layout_component_text))
            else -> ImageViewHolder(inflateView(parent, R.layout.layout_component_image))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                val imageUrl = list[position].picture
                imageLoader
                    .load(imageUrl)
                    .into(holder.imageView)
            }
            is TextViewHolder -> holder.textView.text = list[position].text
        }
    }

    private class TextViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    private class ImageViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

}