package com.android.quo.view.myplaces.createplace


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.model.ServerAddress
import com.android.quo.model.ServerComponent
import com.android.quo.model.ServerPlace
import com.android.quo.model.ServerSettings
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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serverAddress = ServerAddress("","",-1)
        val serverSettings = ServerSettings(false,false)
        val serverComponent = ServerComponent("","","",-1)
        val serverComponents = ArrayList<ServerComponent>()
        serverComponents.add(serverComponent)

        place = ServerPlace("","","","","","",
                "",serverAddress, serverSettings,"","",serverComponents)

        place.copy(title = "createPlace")

                this.context?.let {
            createPlaceViewPager.adapter = CreatePlacePagerAdapter(childFragmentManager, it)
        }
        tabLayout.setupWithViewPager(createPlaceViewPager)

        setupToolbar()

    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.inflateMenu(R.menu.create_place_menu)
        toolbar.title = getString(R.string.new_place)

        compositDisposable.add(
                RxToolbar.navigationClicks(toolbar)
                        .subscribe {
                            activity?.onBackPressed()
                        }
        )

        compositDisposable.add(
                RxToolbar.itemClicks(toolbar)
                        .subscribe {
                            //TODO save
                        }
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        compositDisposable.dispose()
    }
}