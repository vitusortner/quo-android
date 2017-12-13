package com.android.quo.view.place

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.extensions.toPx
import com.android.quo.view.place.info.InfoFragment
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_place.appBarLayout
import kotlinx.android.synthetic.main.fragment_place.imageView
import kotlinx.android.synthetic.main.fragment_place.placeViewPager
import kotlinx.android.synthetic.main.fragment_place.tabLayout
import kotlinx.android.synthetic.main.fragment_place.toolbar


/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceFragment : Fragment() {

    private var place: Place? = null

    private val compositDisposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        place = arguments?.getParcelable("place")

        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // show bottom navigation bar when coming from fragment with hidden bottom nav bar
        if (activity?.bottomNavigationView?.visibility == View.GONE) {
            activity?.bottomNavigationView?.visibility = View.VISIBLE
        }

        setupToolbar()

        this.context?.let { context ->
            placeViewPager.adapter = PlacePagerAdapter(childFragmentManager, context, place?.id)
        }

        tabLayout.setupWithViewPager(placeViewPager)
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.inflateMenu(R.menu.place_menu)
        toolbar.title = place?.title ?: ""

        // TODO else show placeholder https://app.clickup.com/751518/751948/t/w5hm
        val imageUrl = place?.titlePicture ?: ""

        Glide.with(this.context)
                .load(imageUrl)
                .into(imageView)

        compositDisposable.add(
                RxToolbar.navigationClicks(toolbar)
                        .subscribe {
                            activity?.onBackPressed()
                        }
        )

        compositDisposable.add(
                RxToolbar.itemClicks(toolbar)
                        .subscribe {
                            val bundle = Bundle()
                            bundle.putParcelable("place", place)
                            val fragment = InfoFragment()
                            fragment.arguments = bundle

                            fragmentManager?.beginTransaction()
                                    ?.replace(R.id.content, fragment)
                                    ?.addToBackStack(null)
                                    ?.commit()
                        }
        )

        this.context?.let {
            // TODO resolve log spam https://stackoverflow.com/questions/38913215/requestlayout-improperly-called-by-collapsingtoolbarlayout
            // set tab layout colors denpendent on how far scrolled
//            var scrollRange = -1
//
//            appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
//                // set shadow
//                ViewCompat.setElevation(appBarLayout, 4f.toPx(it).toFloat())
//
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.totalScrollRange
//                }
//                if (scrollRange + verticalOffset <= 150) {
//                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTextBlack))
//                    tabLayout.setTabTextColors(resources.getColor(R.color.colorTextBlack),
//                            resources.getColor(R.color.colorTextBlack))
//                } else {
//                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTextWhite))
//                    tabLayout.setTabTextColors(resources.getColor(R.color.colorTextWhite),
//                            resources.getColor(R.color.colorTextWhite))
//                }
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        compositDisposable.dispose()
    }
}