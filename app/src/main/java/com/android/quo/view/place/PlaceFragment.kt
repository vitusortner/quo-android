package com.android.quo.view.place

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.view.place.info.InfoFragment
import com.android.quo.viewmodel.PlaceViewModel
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_place.appBarLayout
import kotlinx.android.synthetic.main.fragment_place.imageView
import kotlinx.android.synthetic.main.fragment_place.placeViewPager
import kotlinx.android.synthetic.main.fragment_place.tabLayout
import kotlinx.android.synthetic.main.fragment_place.toolbar
import android.util.TypedValue
import android.util.DisplayMetrics
import com.android.quo.extensions.toPx


/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceFragment : Fragment() {

    private var viewModel: PlaceViewModel? = null

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
        this.parentFragment?.let {
            viewModel = ViewModelProviders.of(it).get(PlaceViewModel().javaClass)
//            viewModel?.getPlace.observe(it, Observer { place ->
//                    place.title
//                    place.headerImageUrl
//            })
        }

        setupToolbar()

        placeViewPager.adapter = PlacePagerAdapter(childFragmentManager)

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
                // value trough trial and error
                if (scrollRange + verticalOffset <= 150) {
                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.black))
                    tabLayout.setTabTextColors(resources.getColor(R.color.black),
                            resources.getColor(R.color.black))
                } else {
                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.white))
                    tabLayout.setTabTextColors(resources.getColor(R.color.white),
                            resources.getColor(R.color.white))
                }
            }
        }
    }

    // TODO https://app.clickup.com/751518/751948/t/vnbu
    // remove in main activity then
//    override fun onResume() {
//        super.onResume()
//
//        activity?.window?.let { window ->
//            this.context?.let { context ->
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    window.statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
//                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                } else {
//                    window.statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDarkPreM)
//                }
//            }
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//
//        activity?.window?.let {
//            it.statusBarColor = ContextCompat.getColor(this.context!!, R.color.colorPrimaryDark)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()

        compositDisposable.dispose()
    }
}