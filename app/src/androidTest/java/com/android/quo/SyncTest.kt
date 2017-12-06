package com.android.quo

import android.support.test.runner.AndroidJUnit4
import com.android.quo.networking.SyncService
import com.android.quo.networking.model.ServerAddress
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerSettings
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by vitusortner on 05.12.17.
 */
@RunWith(AndroidJUnit4::class)
class SyncTest {

    @Test
    fun testPlacesSync() {
        val component = ServerComponent(
                id = "1",
                picture = "src.com",
                text = null,
                position = 1
        )

        val place = ServerPlace(
                id = "1",
                host = "1",
                title = "title",
                description = "description",
                startDate = "05.12.17",
                endDate = "06.12.17",
                latitude = "12",
                longitude = "21",
                address = ServerAddress("street", "city", 12345),
                settings = ServerSettings(true, true),
                titlePicture = "src.com",
                qrCodeId = "1",
                components = listOf(component)
        )

        SyncService.savePlaces(listOf(place))
    }
}