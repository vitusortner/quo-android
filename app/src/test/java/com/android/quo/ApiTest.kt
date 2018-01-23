package com.android.quo

import com.android.quo.network.ApiClient
import com.android.quo.network.model.ServerComponent
import com.android.quo.network.model.ServerLogin
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by vitusortner on 06.12.17.
 */
class ApiTest {

    private val apiService = ApiClient.instance

    @Test
    fun testLogin() {
        val user = ServerLogin("jdijidjdji@email.com", "jidjdijsijidjsid888")

        apiService.login(user)
                .subscribeOn(Schedulers.io())
                .test()
                .assertNoErrors()
    }

    @Test
    fun testPostComponent() {
        val component = ServerComponent(text = "hi", position = 0)

        apiService.addComponent(component)
                .subscribeOn(Schedulers.io())
                .test()
                .assertNoErrors()
    }
}