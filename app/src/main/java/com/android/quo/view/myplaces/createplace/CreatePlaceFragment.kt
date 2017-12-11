package com.android.quo.view.myplaces.createplace


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.networking.ApiService
import com.android.quo.networking.model.ServerPlace
import com.android.quo.viewmodel.CreatePlaceViewModel
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_create_place.createPlaceViewPager
import kotlinx.android.synthetic.main.fragment_create_place.tabLayout
import kotlinx.android.synthetic.main.fragment_place.toolbar


/**
 * Created by Jung on 27.11.17.
 */

class CreatePlaceFragment : Fragment() {
    private val compositDisposable = CompositeDisposable()
    lateinit var place: ServerPlace
    private val apiService = ApiService.instance
    private lateinit var viewModel: CreatePlaceViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(CreatePlaceViewModel::class.java)

        this.context?.let {
            createPlaceViewPager.adapter = CreatePlacePagerAdapter(childFragmentManager, it)
        }
        tabLayout.setupWithViewPager(createPlaceViewPager)

        /**
         * change status bar color
         */
        activity?.window?.statusBarColor = resources.getColor(R.color.colorAccentDark)

        setupToolbar()

    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.inflateMenu(R.menu.create_place_menu)
        toolbar.title = getString(R.string.new_place)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorTextWhite))


        apiService.addPlace(CreatePlace.place)

        compositDisposable.add(
                RxToolbar.navigationClicks(toolbar)
                        .subscribe {
                            activity?.onBackPressed()
                        }
        )

        compositDisposable.add(
                RxToolbar.itemClicks(toolbar)
                        .subscribe {
                            viewModel.savePlace()
                        }
        )
    }

    private fun savePlace() {
        ApiService.instance.addPlace(CreatePlace.place)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStop() {
        super.onStop()

        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
    }

    override fun onDestroy() {
        super.onDestroy()

        compositDisposable.dispose()
    }
}