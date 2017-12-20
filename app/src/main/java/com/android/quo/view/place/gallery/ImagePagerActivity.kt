package com.android.quo.view.place.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.android.quo.R
import com.android.quo.db.entity.Picture
import com.android.quo.extensions.toPx
import kotlinx.android.synthetic.main.activity_gallery_image_pager.viewPager


/**
 * Created by vitusortner on 16.11.17.
 */
class ImagePagerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_image_pager)

        val list = intent.getParcelableArrayListExtra<Picture>("list")
        val position = intent.getIntExtra("position", 0)

        viewPager.pageMargin = resources.getDimension(R.dimen.place_image_pager_margin).toPx(this)
        viewPager.adapter = ImagePagerAdapter(supportFragmentManager, list)
        viewPager.currentItem = position
    }

    class ImagePagerAdapter(
            fragmentManager: FragmentManager,
            private val list: List<Picture>
    ) : FragmentStatePagerAdapter(fragmentManager) {

        /**
         * Decides which image to show in fragment depending on [position]
         */
        override fun getItem(position: Int): Fragment {
            val fragment = ImageFragment()
            // append url to fragment
            val bundle = Bundle()
            bundle.putString("url", list[position].src)
            fragment.arguments = bundle

            return fragment
        }

        override fun getCount() = list.size
    }
}