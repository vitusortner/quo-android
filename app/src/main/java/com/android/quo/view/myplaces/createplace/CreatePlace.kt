package com.android.quo.view.myplaces.createplace

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.android.quo.Application
import com.android.quo.network.model.ServerComponent
import com.android.quo.network.model.ServerPlace

/**
 * Created by Jung on 05.12.17.
 */

object CreatePlace {
    var place: ServerPlace = ServerPlace(null, Application.database.userDao().getUser().toString(), "", "", "", "",
            -1.0, -1.0, null, null, "quo_default_1.png",
            "", null, "")
    val components = ArrayList<ServerComponent>()
    var list: ArrayList<Drawable>? = null
    lateinit var qrCodeImage: Bitmap

}