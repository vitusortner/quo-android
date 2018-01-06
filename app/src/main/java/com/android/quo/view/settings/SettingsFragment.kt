package com.android.quo.view.settings

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.networking.ApiService
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.RxTextView.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_place.toolbar
import kotlinx.android.synthetic.main.fragment_settings.changePasswordTextView
import kotlinx.android.synthetic.main.layout_change_password.view.newPasswordEditText
import kotlinx.android.synthetic.main.layout_forgot_password.view.emailEditText
import kotlinx.android.synthetic.main.layout_forgot_password.view.emailWrapper
import java.util.concurrent.TimeUnit

/**
 * Created by Jung on 05.01.18.
 */

class SettingsFragment : Fragment() {
    private val compositDisposable = CompositeDisposable()
    private val apiService = ApiService.instance

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        compositDisposable.add(RxView.clicks(changePasswordTextView)
                .subscribe {
                    openDialogChangePassword()
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositDisposable.dispose()
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.title = getString(R.string.settings)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorTextBlack))

        compositDisposable.add(
                RxToolbar.navigationClicks(toolbar)
                        .subscribe {
                            activity?.onBackPressed()
                        }
        )
    }

    /**
     * dialog forgot password
     */
    private fun openDialogChangePassword() {
        this.context?.let {
            val dialog = AlertDialog.Builder(it, R.style.AlertDialogTheme).create()
            val dialogView = layoutInflater.inflate(R.layout.layout_change_password, null)
            dialog.setTitle(resources.getString(R.string.forgot_password))
            dialog.setView(dialogView)

        compositDisposable.add(afterTextChangeEvents(dialogView.newPasswordEditText)
                .skipInitialValue()
                .map {
                    dialogView.emailWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(lengthGreaterThanSix)
                .compose(retryWhenError {
                    dialogView.emailWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(dialogView.emailEditText, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe())

            dialog.setOnShowListener({ dialog ->
                val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                compositDisposable.add(RxView.clicks(buttonNext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (dialogView.emailWrapper.error.isNullOrEmpty()) {
                                dialog.dismiss()

                            }
                        }))
            })

            dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.okay), { _, _ -> })
            dialog.show()
        }
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

    inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }
        }
    }

    /**
     * set Color to red if error
     * set Color to default if no error
     */
    private fun checkEditTextTintColor(message: String?): Int {
        var backgroundTintColor: Int = R.style.EditTextTheme
        if (!message.isNullOrEmpty()) {
            backgroundTintColor = R.color.colorAlert
        }
        return backgroundTintColor
    }
}