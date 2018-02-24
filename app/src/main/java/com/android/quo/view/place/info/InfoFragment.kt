package com.android.quo.view.place.info

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.util.Constants.Extra
import com.android.quo.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_info.imageView
import kotlinx.android.synthetic.main.fragment_info.recyclerView
import kotlinx.android.synthetic.main.fragment_info.toolbar

/**
 * Created by vitusortner on 21.11.17.
 */
class InfoFragment : BaseFragment(R.layout.fragment_info) {

    private var place: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        place = arguments?.getParcelable(Extra.PLACE_EXTRA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        // TODO else show placeholder https://app.clickup.com/751518/751948/t/w5hm
        val imageUrl = place?.titlePicture ?: ""
        imageLoader
            .load(imageUrl)
            .into(imageView)

        place?.let {
            recyclerView.adapter = InfoAdapter(it)
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.title = place?.title ?: ""

        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }
}