package com.android.quo.view

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by vitusortner on 24.02.18.
 */
abstract class BaseRecyclerViewAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = emptyList<T>()

    fun setItems(items: List<T>) {
        list = items
        notifyDataSetChanged()
    }

    final override fun getItemCount() = list.size

    fun inflateView(parent: ViewGroup, @LayoutRes layout: Int) =
        LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer
}