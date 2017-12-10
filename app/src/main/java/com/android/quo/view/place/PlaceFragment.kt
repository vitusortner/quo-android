package com.android.quo.view.place

import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.QuoApplication
import com.android.quo.R
import com.android.quo.extensions.toPx
import com.android.quo.networking.ApiService
import com.android.quo.networking.PictureRepository
import com.android.quo.networking.SyncService
import com.android.quo.view.place.info.InfoFragment
import com.android.quo.viewmodel.PlaceViewModel
import com.android.quo.viewmodel.factory.PlaceViewModelFactory
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_place.appBarLayout
import kotlinx.android.synthetic.main.fragment_place.collapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_place.imageView
import kotlinx.android.synthetic.main.fragment_place.placeViewPager
import kotlinx.android.synthetic.main.fragment_place.tabLayout
import kotlinx.android.synthetic.main.fragment_place.toolbar


/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceFragment : Fragment() {

    private val compositDisposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // show bottom navigation bar when coming from fragment with hidden bottom nav bar
        if (activity?.bottomNavigationView?.visibility == View.GONE) {
            activity?.bottomNavigationView?.visibility = View.VISIBLE
        }

        // TODO proper viewmodel handling
//            viewModel?.getPlace.observe(it, Observer { place ->
//                    place.title
//                    place.headerImageUrl
//            })

        setupToolbar()

        this.context?.let {
            placeViewPager.adapter = PlacePagerAdapter(childFragmentManager, it)
        }

        tabLayout.setupWithViewPager(placeViewPager)
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.inflateMenu(R.menu.place_menu)

        // TODO add real title
        toolbar.title = "Lorem ipsum"

        // TODO add real image
        // viewModel.headerImageUrl
        Glide.with(this.context)
                .load("https://static.pexels.com/photos/196643/pexels-photo-196643.jpeg")
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
                            fragmentManager?.beginTransaction()
                                    ?.replace(R.id.content, InfoFragment())
                                    ?.addToBackStack(null)
                                    ?.commit()
                        }
        )

        this.context?.let {
            // TODO resolve log spam https://stackoverflow.com/questions/38913215/requestlayout-improperly-called-by-collapsingtoolbarlayout
            // set tab layout colors denpendent on how far scrolled
            var scrollRange = -1

            appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                // set shadow
                ViewCompat.setElevation(appBarLayout, 4f.toPx(it).toFloat())

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset <= 150) {
                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTextBlack))
                    tabLayout.setTabTextColors(resources.getColor(R.color.colorTextBlack),
                            resources.getColor(R.color.colorTextBlack))
                } else {
                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTextWhite))
                    tabLayout.setTabTextColors(resources.getColor(R.color.colorTextWhite),
                            resources.getColor(R.color.colorTextWhite))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        compositDisposable.dispose()
    }
}