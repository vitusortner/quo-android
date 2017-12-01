package com.android.quo.view.login


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.android.quo.QuoApplication
import com.android.quo.R
import com.android.quo.R.style.EditTextTheme
import com.android.quo.networking.ApiService
import com.android.quo.networking.PlaceRepository
import com.android.quo.view.main.MainActivity
import com.android.quo.viewmodel.LoginViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.clickableForgotPasswordTextView
import kotlinx.android.synthetic.main.activity_login.emailEditText
import kotlinx.android.synthetic.main.activity_login.emailWrapper
import kotlinx.android.synthetic.main.activity_login.loginButton
import kotlinx.android.synthetic.main.activity_login.passwordEditText
import kotlinx.android.synthetic.main.activity_login.passwordWrapper
import kotlinx.android.synthetic.main.activity_login.signUpButton
import kotlinx.android.synthetic.main.layout_forgot_password.view.emailEditText
import kotlinx.android.synthetic.main.layout_forgot_password.view.emailWrapper
import kotlinx.android.synthetic.main.layout_sign_up.view.agreementCheckbox
import kotlinx.android.synthetic.main.layout_sign_up.view.emailSignUpEditText
import kotlinx.android.synthetic.main.layout_sign_up.view.passwordSignUpEditText
import kotlinx.android.synthetic.main.layout_sign_up.view.passwordWrapper
import java.util.concurrent.TimeUnit


/**
 * Created by Jung on 09.11.17.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var callbackManager: CallbackManager
    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        callbackManager = CallbackManager.Factory.create()
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel().javaClass)

        PlaceRepository(QuoApplication.database.placeDao(), ApiService.instance)

        /**
         * handle Facebook result
         */
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        // App code
                    }

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                    }
                })

        /**
         * check if email is validate
         */
        compositeDisposable.add(RxTextView.afterTextChangeEvents(emailEditText)
                .skipInitialValue()
                .map {
                    emailWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.verifyEmailPattern)
                .compose(loginViewModel.retryWhenError {
                    emailWrapper.error = it.message

                    ViewCompat.setBackgroundTintList(emailEditText, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe())

        /**
         * check if password is validate
         */
        compositeDisposable.add(RxTextView.afterTextChangeEvents(passwordEditText)
                .skipInitialValue()
                .map {
                    passwordWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.lengthGreaterThanSix)
                .compose(loginViewModel.retryWhenError {
                    passwordWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(emailEditText, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe())

        /**
         * button click handler for signIn
         */
        compositeDisposable.add(RxView.clicks(signUpButton)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { openDialogSignUp() })

        /**
         * button click handler for login button
         */
        compositeDisposable.add(RxView.clicks(loginButton)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (emailWrapper.error.isNullOrEmpty() && passwordWrapper.error.isNullOrEmpty()) {
                        //TODO login user

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                })

        /**
         * button click handler for forgot password
         */
        compositeDisposable.add(RxView.clicks(clickableForgotPasswordTextView)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ openDialogForgotPassword() }))
    }

    /**
     * set Color to red if error
     * set Color to default if no error
     */
    private fun checkEditTextTintColor(message: String?): Int {
        var backgroundTintColor: Int = EditTextTheme
        if (!message.isNullOrEmpty()) {
            backgroundTintColor = R.color.colorAlert
        }
        return backgroundTintColor
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    /**
     * dialog forgot password
     */
    private fun openDialogForgotPassword() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        val dialogView = layoutInflater.inflate(R.layout.layout_forgot_password, null)
        dialog.setTitle(resources.getString(R.string.forgot_password))
        dialog.setView(dialogView)

        compositeDisposable.add(RxTextView.afterTextChangeEvents(dialogView.emailEditText)
                .skipInitialValue()
                .map {
                    dialogView.emailWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.verifyEmailPattern)
                .compose(loginViewModel.retryWhenError {
                    dialogView.emailWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(dialogView.emailEditText, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe())

        dialog.setOnShowListener({ dialog ->
            val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            compositeDisposable.add(RxView.clicks(buttonNext)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (dialogView.emailWrapper.error.isNullOrEmpty()) {
                            dialog.dismiss()
                            openDialogPasswordResetFinished()
                        }
                    }))
        })

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.next), { _, _ -> })
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.cancel), { _, _ -> })
        dialog.show()
    }

    /**
     * dialog finished reset password
     */
    private fun openDialogPasswordResetFinished() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        dialog.setTitle(resources.getString(R.string.send_reset_email_title))
        dialog.setMessage(resources.getString(R.string.send_reset_email_message))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.okay), { _, _ -> })
        dialog.show()
    }

    /**
     * dialog for SignUp
     */
    private fun openDialogSignUp() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        val dialogView = layoutInflater.inflate(R.layout.layout_sign_up, null)
        dialog.setTitle(resources.getString(R.string.sign_up))
        dialog.setView(dialogView)

        if (emailEditText.text.isNotEmpty()) {
            dialogView.emailSignUpEditText.text = emailEditText.text
        }
        if (passwordEditText.text.isNotEmpty()) {
            dialogView.passwordSignUpEditText.text = passwordEditText.text
        }

        //check if email is validate
        compositeDisposable.add(RxTextView.afterTextChangeEvents(dialogView.emailSignUpEditText)
                .skipInitialValue()
                .map {
                    dialogView.emailWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.verifyEmailPattern)
                .compose(loginViewModel.retryWhenError {
                    dialogView.emailWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(dialogView.emailSignUpEditText, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe())

        //check if password is validate
        compositeDisposable.add(RxTextView.afterTextChangeEvents(dialogView.passwordSignUpEditText)
                .skipInitialValue()
                .map {
                    dialogView.passwordWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.lengthGreaterThanSix)
                .compose(loginViewModel.retryWhenError {
                    dialogView.passwordWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(dialogView.passwordSignUpEditText, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe())

        dialog.setOnShowListener({ dialog ->
            val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            compositeDisposable.add(RxView.clicks(buttonNext)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (dialogView.agreementCheckbox.isChecked) {
                            dialog.dismiss()
                            //TODO add new account to db
                            //TODO Check if password and email have no errors
                        } else {
                            dialogView.agreementCheckbox.setTextColor(getColor(R.color.colorAlert))
                        }
                    })
        })

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.next), { _, _ -> })

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.cancel), { _, _ -> })
        dialog.show()
    }
}