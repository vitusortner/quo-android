package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.networking.AuthService
import com.android.quo.viewmodel.LoginViewModel

/**
 * Created by vitusortner on 04.01.18.
 */
class LoginViewModelFactory(private val authService: AuthService) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(authService) as T
    }
}