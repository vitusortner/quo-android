package com.android.quo.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.android.quo.R

object DefaultImages {

    fun get(context: Context): ArrayList<Drawable> {
        val list = arrayListOf<Drawable>()
        listOf(
            R.drawable.default_event_image1,
            R.drawable.default_event_image2,
            R.drawable.default_event_image3,
            R.drawable.default_event_image4,
            R.drawable.default_event_image5,
            R.drawable.default_event_image6,
            R.drawable.default_event_image7,
            R.drawable.default_event_image8,
            R.drawable.default_event_image9
        )
            .forEach {
                ContextCompat.getDrawable(context, it)?.let { list.add(it) }
            }
        return list
    }

}