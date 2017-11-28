package com.android.quo.view.myplaces

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.android.quo.R
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_create_page.floatingActionButton
import kotlinx.android.synthetic.main.fragment_create_page.mainEditText
import kotlinx.android.synthetic.main.fragment_create_page.roundEditButton
import kotlinx.android.synthetic.main.fragment_create_page.roundGalleryButton
import kotlinx.android.synthetic.main.fragment_create_page.tempLine1TextView
import kotlinx.android.synthetic.main.fragment_create_page.tempLine2TextView


/**
 * Created by Jung on 27.11.17.
 */

class CreatePageFragment : Fragment() {
    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable.add(RxView.clicks(floatingActionButton)
                .subscribe {
                    roundEditButton.visibility = VISIBLE
                    roundGalleryButton.visibility = VISIBLE
                })

        compositeDisposable.add(RxView.clicks(mainEditText)
                .subscribe {
                    if (roundEditButton.isEnabled && roundGalleryButton.isEnabled)
                        roundEditButton.visibility = GONE
                    roundGalleryButton.visibility = GONE
                })

        compositeDisposable.add(RxView.clicks(roundEditButton)
                .subscribe {
                    tempLine1TextView.visibility = GONE
                    tempLine2TextView.visibility = GONE

                    mainEditText.isClickable = true
                    mainEditText.isFocusable = true
                    mainEditText.isFocusableInTouchMode = true
                    mainEditText.isCursorVisible = true
                })


    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
