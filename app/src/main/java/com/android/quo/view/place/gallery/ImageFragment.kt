package com.android.quo.view.place.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.alexvasilkov.gestures.GestureController
import com.android.quo.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_gallery_image.imageView
import kotlinx.android.synthetic.main.fragment_gallery_image.toolbar

/**
 * Created by vitusortner on 16.11.17.
 */
class ImageFragment : Fragment() {

    private var url: String? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            url = it.getString("url")
        }

        return inflater.inflate(R.layout.fragment_gallery_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        url?.let {
            Glide.with(context)
                    .load(it)
                    .into(imageView)
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        // handle tap on image
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
                activity?.let { activity ->
                    val decorView = activity.window.decorView

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
                }
                return true
            }
        })
    }
}