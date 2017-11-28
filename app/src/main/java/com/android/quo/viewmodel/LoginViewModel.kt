package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Patterns
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single

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

    val lengthGreaterThanSix = ObservableTransformer<String, String> { observable ->
        observable.flatMap {
            Observable.just(it).map { it.trim() } // - abcdefg - |
                    .filter { it.length > 6 }
                    .singleOrError()
                    .onErrorResumeNext {
                        if (it is NoSuchElementException) {
                            Single.error(Exception("Length should be greater than 6"))
                        } else {
                            Single.error(it)
                        }
                    }
                    .toObservable()
        }
    }

    val verifyEmailPattern = ObservableTransformer<String, String> { observable ->
        observable.flatMap {
            Observable.just(it).map { it.trim() }
                    .filter {
                        Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    }
                    .singleOrError()
                    .onErrorResumeNext {
                        if (it is NoSuchElementException) {
                            Single.error(Exception("Email not valid"))
                        } else {
                            Single.error(it)
                        }
                    }
                    .toObservable()
        }
    }

    inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }
        }
    }
}