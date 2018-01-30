package com.android.quo.view.createplace.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.util.CreatePlace
import kotlinx.android.synthetic.main.fragment_create_settings.gpsSwitch
import kotlinx.android.synthetic.main.fragment_create_settings.photosSwitch

/**
 * Created by Jung on 27.11.17.
 */
class CreateSettingsFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_settings, container, false)

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