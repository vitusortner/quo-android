package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Patterns

/**
 * Created by Jung on 09.11.17.
 */
class LoginViewModel : ViewModel() {

    fun handleRegister(username: String, password: String): Boolean {
        //TODO check if user already exist
        //TODO register user
        //TODO return true or false after registration
        return true
    }

    fun sendEmailToUser(email: String): Boolean {
        //TODO check if email exist
        //TODO send email to user
        //TODO return true or false after email send
        return true
    }

    fun verifyEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun verifyPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= 6
    }

}