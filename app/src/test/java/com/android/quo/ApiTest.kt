package com.android.quo

import com.android.quo.networking.service.ApiService
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerLogin
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by vitusortner on 06.12.17.
 */
class ApiTest {

    private val apiService = ApiService.instance

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