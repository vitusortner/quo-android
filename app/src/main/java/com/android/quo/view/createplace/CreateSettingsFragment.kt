package com.android.quo.view.createplace

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_create_settings.gpsSwitch
import kotlinx.android.synthetic.main.fragment_create_settings.photosSwitch

/**
 * Created by Jung on 27.11.17.
 */

class CreateSettingsFragment : Fragment() {
    private var compositeDisposable = CompositeDisposable()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(RxCompoundButton.checkedChanges(gpsSwitch)
                .subscribe { checked ->
                    CreatePlace.place.settings?.hasToValidateGps = checked
                })

        compositeDisposable.add(RxCompoundButton.checkedChanges(photosSwitch)
                .subscribe { checked ->
                    CreatePlace.place.settings?.isPhotoUploadAllowed = checked
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}