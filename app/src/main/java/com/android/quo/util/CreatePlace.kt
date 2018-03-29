package com.android.quo.util

import android.graphics.Bitmap
import com.android.quo.network.model.ServerComponent
import com.android.quo.network.model.ServerPlace
import com.android.quo.network.model.ServerSettings

/**
 * Created by Jung on 05.12.17.
 */
object CreatePlace {

    var place = initPlace()

    val components = ArrayList<ServerComponent>()

    lateinit var qrCodeImage: Bitmap

    fun reset() {
        place = initPlace()
    }

    private fun initPlace() =
        ServerPlace(
            host = "",
            title = "",
            startDate = "",
            latitude = -1.0,
            longitude = -1.0,
            settings = ServerSettings(false, false),
            titlePicture = "quo_default_1.png",
            qrCodeId = "",
            timestamp = ""
        )
}