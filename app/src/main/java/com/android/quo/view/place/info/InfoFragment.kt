package com.android.quo.view.place.info

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.widget.RxToolbar
import kotlinx.android.synthetic.main.fragment_info.imageView
import kotlinx.android.synthetic.main.fragment_info.recyclerView
import kotlinx.android.synthetic.main.fragment_info.toolbar

/**
 * Created by vitusortner on 21.11.17.
 */
class InfoFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO get viewmodel, observe relevant information

        setupToolbar()

        Glide.with(this.context)
                .load("https://static.pexels.com/photos/196643/pexels-photo-196643.jpeg")
                .into(imageView)

        recyclerView.adapter = InfoAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        // TODO get title from viewmodel
        toolbar.title = "Lorem ipsum"

        RxToolbar.navigationClicks(toolbar)
                .subscribe {
                    activity?.onBackPressed()
                }
    }
}