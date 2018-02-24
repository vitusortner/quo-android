package com.android.quo.view.createplace.settings

import android.os.Bundle
import android.view.View
import com.android.quo.R
import com.android.quo.util.CreatePlace
import com.android.quo.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_settings.gpsSwitch
import kotlinx.android.synthetic.main.fragment_create_settings.photosSwitch

/**
 * Created by Jung on 27.11.17.
 */
class CreateSettingsFragment : BaseFragment(R.layout.fragment_create_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gpsSwitch.setOnCheckedChangeListener { _, isChecked ->
            CreatePlace.place.settings?.hasToValidateGps = isChecked
        }
        photosSwitch.setOnCheckedChangeListener { _, isChecked ->
            CreatePlace.place.settings?.isPhotoUploadAllowed = isChecked
        }
    }
}