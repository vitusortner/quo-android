package com.android.quo.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.android.quo.R

object DefaultImages {

    fun get(context: Context): ArrayList<Drawable> {
        val list = ArrayList<Drawable>()
        ContextCompat.getDrawable(context, R.drawable.default_event_image1)
            ?.let { list.add(it) }
        ContextCompat.getDrawable(context, R.drawable.default_event_image2)
            ?.let { list.add(it) }
        ContextCompat.getDrawable(context, R.drawable.default_event_image3)
            ?.let { list.add(it) }
        ContextCompat.getDrawable(context, R.drawable.default_event_image4)
            ?.let { list.add(it) }
        ContextCompat.getDrawable(context, R.drawable.default_event_image5)
            ?.let { list.add(it) }
        ContextCompat.getDrawable(context, R.drawable.default_event_image6)
            ?.let { list.add(it) }
        ContextCompat.getDrawable(context, R.drawable.default_event_image7)
            ?.let { list.add(it) }
        ContextCompat.getDrawable(context, R.drawable.default_event_image8)
            ?.let { list.add(it) }
        ContextCompat.getDrawable(context, R.drawable.default_event_image9)
            ?.let { list.add(it) }
        return list
    }

}