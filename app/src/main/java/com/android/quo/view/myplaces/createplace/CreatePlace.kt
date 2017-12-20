package com.android.quo.view.myplaces.createplace

import android.graphics.Bitmap
import com.android.quo.networking.model.ServerAddress
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerSettings

/**
 * Created by Jung on 05.12.17.
 */

object CreatePlace {
    var place: ServerPlace
    val components = ArrayList<ServerComponent>()
    lateinit var qrCode: Bitmap

    init {
        val serverAddress = ServerAddress("", "", -1)
        val serverSettings = ServerSettings(false, false)

        val emptyComponents = ArrayList<ServerComponent>()
        place = ServerPlace(null, "", "", "", "", "",
                -1.0, -1.0, serverAddress, serverSettings, "1", "", emptyComponents)


    }


}