package com.android.quo.view.place.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.alexvasilkov.gestures.GestureController
import com.android.quo.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.gallery_image_full.imageView

/**
 * Created by vitusortner on 16.11.17.
 */
class ImageFragment : Fragment() {

    private lateinit var url: String

    /**
     * Retrieves string extra from bundle and inflates view
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        url = arguments.getString("url")

        return inflater.inflate(R.layout.gallery_image_full, container, false)
    }

    /**
     * Loads image from URL into image view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide.with(view.context)
                .load(url)
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(imageView)

        imageView.controller.setOnGesturesListener(object : GestureController.OnGestureListener {
            override fun onSingleTapUp(event: MotionEvent) = false

            override fun onDown(event: MotionEvent) {
            }

            override fun onDoubleTap(event: MotionEvent) = false

            override fun onUpOrCancel(event: MotionEvent) {
            }

            override fun onLongPress(event: MotionEvent) {
            }

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                val decorView = activity.window.decorView
                val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                decorView.systemUiVisibility = uiOptions
//                activity.actionBar.hide()
                return true
            }
        })
    }
}