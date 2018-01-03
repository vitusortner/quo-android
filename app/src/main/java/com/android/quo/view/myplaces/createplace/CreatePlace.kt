package com.android.quo.view.myplaces.createplace

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerPlace

/**
 * Created by Jung on 05.12.17.
 */

object CreatePlace {
    lateinit var place: ServerPlace
    val components = ArrayList<ServerComponent>()
    lateinit var qrCodeImage: Bitmap
    var list: ArrayList<Drawable>? = null

    init {
        place =  ServerPlace(null, "", "", "", "", "",
                -1.0, -1.0, null, null, "quo_default_1.png",
                "", null, null, null, "")

    }
}