package com.android.quo.view.place.info

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.widget.RxToolbar
import kotlinx.android.synthetic.main.fragment_info.imageView
import kotlinx.android.synthetic.main.fragment_info.recyclerView
import kotlinx.android.synthetic.main.fragment_info.toolbar

/**
 * Created by vitusortner on 21.11.17.
 */
class InfoFragment : Fragment() {

    private var place: Place? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        place = arguments?.getParcelable("place")

        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()

        val imageUrl = place?.titlePicture ?: ""

        Glide.with(this.context)
                .load(imageUrl)
                .into(imageView)

        recyclerView.adapter = InfoAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.title = place?.title ?: ""

        RxToolbar.navigationClicks(toolbar)
                .subscribe {
                    activity?.onBackPressed()
                }
    }
}