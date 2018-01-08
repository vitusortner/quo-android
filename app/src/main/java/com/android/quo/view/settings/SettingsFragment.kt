package com.android.quo.view.settings

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.android.quo.R
import com.android.quo.networking.ApiService
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView.afterTextChangeEvents
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.view.passwordEditText
import kotlinx.android.synthetic.main.fragment_place.toolbar
import kotlinx.android.synthetic.main.fragment_settings.changePasswordTextView
import kotlinx.android.synthetic.main.fragment_settings.deleteAccountTextView
import kotlinx.android.synthetic.main.layout_change_password.view.newPasswordEditText
import kotlinx.android.synthetic.main.layout_change_password.view.newPasswordWrapper
import kotlinx.android.synthetic.main.layout_change_password.view.oldPasswordEditText
import kotlinx.android.synthetic.main.layout_change_password.view.passwordWrapper
import kotlinx.android.synthetic.main.layout_change_password.view.repeatNewPasswordEditText
import kotlinx.android.synthetic.main.layout_change_password.view.repeatNewPasswordWrapper
import java.util.concurrent.TimeUnit

/**
 * Created by Jung on 05.01.18.
 */

class SettingsFragment : Fragment() {
    private val compositDisposable = CompositeDisposable()
    private val apiService = ApiService.instance
    private lateinit var passwordEditText: EditText

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

        compositDisposable.add(RxView.clicks(deleteAccountTextView)
                .subscribe {
                    openDialogDeleteAccount()
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
     * dialog change password
     */
    private fun openDialogChangePassword() {
        this.context?.let {
            val dialog = AlertDialog.Builder(it, R.style.AlertDialogTheme).create()
            val dialogView = layoutInflater.inflate(R.layout.layout_change_password, null)
            dialog.setTitle(resources.getString(R.string.change_password))
            dialog.setView(dialogView)
            passwordEditText = dialogView.newPasswordEditText

            compositDisposable.add(afterTextChangeEvents(dialogView.oldPasswordEditText)
                    .skipInitialValue()
                    .map {
                        dialogView.passwordWrapper.error = null
                        it.view().text.toString()
                    }
                    .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .compose(lengthGreaterThanSix)
                    .compose(retryWhenError {
                        dialogView.passwordWrapper.error = it.message
                        ViewCompat.setBackgroundTintList(dialogView.oldPasswordEditText, ColorStateList
                                .valueOf(checkEditTextTintColor(it.message)))
                    })
                    .subscribe())

            compositDisposable.add(afterTextChangeEvents(dialogView.newPasswordEditText)
                    .skipInitialValue()
                    .map {
                        dialogView.newPasswordWrapper.error = null
                        it.view().text.toString()
                    }
                    .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .compose(lengthGreaterThanSix)
                    .compose(retryWhenError {
                        dialogView.newPasswordWrapper.error = it.message
                        ViewCompat.setBackgroundTintList(dialogView.newPasswordEditText, ColorStateList
                                .valueOf(checkEditTextTintColor(it.message)))
                    })
                    .subscribe())

            compositDisposable.add(afterTextChangeEvents(dialogView.repeatNewPasswordEditText)
                    .skipInitialValue()
                    .map {
                        dialogView.repeatNewPasswordWrapper.error = null
                        it.view().text.toString()
                    }
                    .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .compose(lengthGreaterThanSix)
                    .compose(passwordEquality)
                    .compose(retryWhenError {
                        dialogView.repeatNewPasswordWrapper.error = it.message
                        ViewCompat.setBackgroundTintList(dialogView.repeatNewPasswordEditText, ColorStateList
                                .valueOf(checkEditTextTintColor(it.message)))
                    })
                    .subscribe())

            dialog.setOnShowListener({ dialog ->
                val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                compositDisposable.add(RxView.clicks(buttonNext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (dialogView.passwordWrapper.error.isNullOrEmpty()
                                    && dialogView.newPasswordWrapper.error.isNullOrEmpty()
                                    && dialogView.repeatNewPasswordWrapper.error.isNullOrEmpty()) {
                                //TODO check if old password correct and change password
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

    val passwordEquality = ObservableTransformer<String, String> { observable ->
        observable.flatMap {
            Observable.just(it).map { it.trim() }
                    .filter { it == passwordEditText.editableText.toString() }
                    .singleOrError()
                    .onErrorResumeNext {
                        if (it is NoSuchElementException) {
                            Single.error(Exception("Passwords are different"))
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

    private fun openDialogDeleteAccount(){
        this.context?.let {
            val dialog = AlertDialog.Builder(it, R.style.AlertDialogTheme).create()
            dialog.setMessage(resources.getString(R.string.delete_account_text))
            dialog.setOnShowListener({ dialog ->
                val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                compositDisposable.add(RxView.clicks(buttonNext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //TODO DELETE ACCOUNT
                            openDialogDeleteAccountSuccessful()
                                dialog.dismiss()
                        }))
            })

            dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.yes), { _, _ -> })
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.no), { _, _ -> })
            dialog.show()
        }
    }

    private fun openDialogDeleteAccountSuccessful(){
        this.context?.let {
            val dialog = AlertDialog.Builder(it, R.style.AlertDialogTheme).create()
            dialog.setTitle("${resources.getString(R.string.okay)}!")
            dialog.setMessage(resources.getString(R.string.delete_account_successful))
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.okay), { _, _ -> })

            dialog.setOnShowListener({ dialog ->
                val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                compositDisposable.add(RxView.clicks(buttonNext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            // TODO GO TO LOGIN PAGE
                            dialog.dismiss()
                        }))
            })
            dialog.show()
        }
    }
}