package com.android.quo.view.myplaces

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import kotlinx.android.synthetic.main.fragment_my_places.floatingActionButton

/**
 * Created by Jung on 27.11.17.
 */

class CreatePageFragment: Fragment(){
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFloatingActionButton()
    }


    private fun setupFloatingActionButton() =
            floatingActionButton.setOnClickListener {
                Snackbar.make(floatingActionButton, "Floating action button clicked",
                        Snackbar.LENGTH_LONG)
                        .setAction("HIDE", { })
                        .show()
            }
}