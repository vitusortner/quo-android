package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.view.qrcode.QrCodeScannerActivity
import com.android.quo.view.timeline.PlacePreviewAdapter
import com.google.zxing.NotFoundException
import com.google.zxing.Result


/**
 * Created by Jung on 30.10.17.
 */

class QrCodeScannerViewModel : ViewModel() {


    fun handleQrCode(url: Result?, parent: QrCodeScannerActivity){
        try {
            if (url.toString().contains("http")){
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(parent, Uri.parse(url.toString()))
            } else {
                //TODO activity with not found url
                //TODO open places page after the code is scanned
            }

        } catch (e: NotFoundException){
            Log.e("Error", e.message)
        }
    }

}