package com.android.quo.view.myplaces.createplace

import android.graphics.Bitmap
import com.android.quo.networking.model.ServerAddress
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerSettings

/**
 * Created by Jung on 05.12.17.
 */

object CreatePlace {
    var place: ServerPlace
    val components = ArrayList<ServerComponent>()
    val pictures = ArrayList<ServerPicture>()
    lateinit var qrCode: Bitmap

    init {
        val serverAddress = ServerAddress("","",-1)
        val serverSettings = ServerSettings(false,false)
        val serverComponent = ServerComponent("","","",-1)
        val serverComponents = ArrayList<ServerComponent>()
        serverComponents.add(serverComponent)

        place = ServerPlace("","","","","","",
                "","", serverAddress,serverSettings,"","", serverComponents)

       // place = place.copy(title = "createPlace")

    }



}