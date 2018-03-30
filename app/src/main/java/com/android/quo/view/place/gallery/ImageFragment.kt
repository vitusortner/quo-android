package com.android.quo.view.place.gallery

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.alexvasilkov.gestures.GestureController
import com.android.quo.R
import com.android.quo.util.Constants.Extra
import com.android.quo.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_gallery_image.imageView
import kotlinx.android.synthetic.main.fragment_gallery_image.toolbar

/**
 * Created by vitusortner on 16.11.17.
 */
class ImageFragment : BaseFragment(R.layout.fragment_gallery_image) {

    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { url = it.getString(Extra.PICTURE_URL_EXTRA) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        url?.let {
            imageLoader
                .load(it)
                .into(imageView)
        }
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)

        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

        // handle tap on image
        imageView.controller.setOnGesturesListener(observer)
    }

    private val observer = object : GestureController.OnGestureListener {
        override fun onSingleTapUp(event: MotionEvent) = false

        override fun onDown(event: MotionEvent) {
        }

        override fun onDoubleTap(event: MotionEvent) = false

        override fun onUpOrCancel(event: MotionEvent) {
        }

        override fun onLongPress(event: MotionEvent) {
        }

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            val decorView = requireActivity().window.decorView

            if ((decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                decorView.systemUiVisibility =
                        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LOW_PROFILE
                                or View.SYSTEM_UI_FLAG_IMMERSIVE)
                toolbar.visibility = View.GONE
            } else {
                decorView.systemUiVisibility =
                        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                toolbar.visibility = View.VISIBLE
            }
            return true
        }
    }

}