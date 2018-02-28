package com.android.quo.viewmodel

import android.os.AsyncTask
import android.util.Patterns
import com.android.quo.repository.UserRepository
import com.android.quo.service.AuthService
import com.android.quo.util.extension.addTo
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by Jung on 09.11.17.
 */
class LoginViewModel(
    private val authService: AuthService,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    fun sendEmailToUser(email: String): Boolean {
        //TODO permissionGranted if email exist
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

    inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> =
        ObservableTransformer { observable ->
            observable.retryWhen { errors ->
                errors.flatMap {
                    onError(it)
                    Observable.just("")
                }
            }
        }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        authService.login(email, password)
            .subscribeOnIo()
            .observeOnUi()
            .subscribeBy(
                onSuccess = {
                    callback(true)
                },
                onError = {
                    log.e("Error while login", it)
                    callback(false)
                }
            )
            .addTo(compositeDisposable)
    }

    fun signup(email: String, password: String, callback: (Boolean) -> Unit) {
        authService.signup(email, password)
            .subscribeOnIo()
            .observeOnUi()
            .subscribeBy(
                onSuccess = {
                    callback(true)
                },
                onError = {
                    log.e("Error while signup", it)
                    callback(false)
                }
            )
            .addTo(compositeDisposable)
    }

    fun validateLoginState(onSuccess: () -> Unit, onError: () -> Unit) =
        AsyncTask.execute {
            if (userRepository.getUser() == null) onError() else onSuccess()
        }
}