package com.android.quo.view.myplaces.createplace

import com.android.quo.model.ServerAddress
import com.android.quo.model.ServerComponent
import com.android.quo.model.ServerPlace
import com.android.quo.model.ServerSettings

/**
 * Created by Jung on 05.12.17.
 */

object CreatePlace {
    var place: ServerPlace
    val components = java.util.ArrayList<ServerComponent>()

    init {
        val serverAddress = ServerAddress("","",-1)
        val serverSettings = ServerSettings(false,false)
        val serverComponent = ServerComponent("","","",-1)
        val serverComponents = ArrayList<ServerComponent>()
        serverComponents.add(serverComponent)

        place = ServerPlace("","","","","","",
                "",serverAddress, serverSettings,"","",serverComponents)

        place = place.copy(title = "createPlace")

    }

    fun savePlace() {

    }

}