package com.android.quo.view.login

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.view.View
import com.android.quo.MainActivity
import com.android.quo.R
import com.android.quo.R.style.EditTextTheme
import com.android.quo.view.BaseActivity
import com.android.quo.viewmodel.LoginViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
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
import org.koin.android.architecture.ext.viewModel
import java.util.concurrent.TimeUnit

/**
 * Created by Jung on 09.11.17.
 */
class LoginActivity : BaseActivity() {

    private val viewModel by viewModel<LoginViewModel>()

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        callbackManager = CallbackManager.Factory.create()

        handleFacebookLogin()
        validateLoginEmail()
        validateLoginPassword()

        setupButtons()
    }

    private fun setupButtons() {
        clickableForgotPasswordTextView.setOnClickListener { openDialogForgotPassword() }

        loginButton.setOnClickListener {
            if (emailWrapper.error.isNullOrEmpty() && passwordWrapper.error.isNullOrEmpty()) {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                login(email, password)
            }
        }

        signUpButton.setOnClickListener { openDialogSignUp() }
    }


    private fun validateLoginPassword() =
        RxTextView.afterTextChangeEvents(passwordEditText)
            .skipInitialValue()
            .map {
                passwordWrapper.error = null
                it.view().text.toString()
            }
            .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(viewModel.lengthGreaterThanSix)
            .compose(viewModel.retryWhenError {
                passwordWrapper.error = it.message
                ViewCompat.setBackgroundTintList(
                    emailEditText, ColorStateList
                        .valueOf(checkEditTextTintColor(it.message))
                )
            })
            .subscribe()
            .addTo(compositeDisposable)


    private fun validateLoginEmail() =
        RxTextView.afterTextChangeEvents(emailEditText)
            .skipInitialValue()
            .map {
                emailWrapper.error = null
                it.view().text.toString()
            }
            .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(viewModel.verifyEmailPattern)
            .compose(viewModel.retryWhenError {
                emailWrapper.error = it.message

                ViewCompat.setBackgroundTintList(
                    emailEditText, ColorStateList
                        .valueOf(checkEditTextTintColor(it.message))
                )
            })
            .subscribe()
            .addTo(compositeDisposable)

    private fun handleFacebookLogin() {
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

    /**
     * dialog forgot password
     */
    private fun openDialogForgotPassword() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        val dialogView = layoutInflater.inflate(R.layout.layout_forgot_password, null)
        dialog.setTitle(resources.getString(R.string.forgot_password))
        dialog.setView(dialogView)

        RxTextView.afterTextChangeEvents(dialogView.emailEditText)
            .skipInitialValue()
            .map {
                dialogView.emailWrapper.error = null
                it.view().text.toString()
            }
            .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(viewModel.verifyEmailPattern)
            .compose(viewModel.retryWhenError {
                dialogView.emailWrapper.error = it.message
                ViewCompat.setBackgroundTintList(
                    dialogView.emailEditText, ColorStateList
                        .valueOf(checkEditTextTintColor(it.message))
                )
            })
            .subscribe()
            .addTo(compositeDisposable)

        dialog.setOnShowListener({ dialog ->
            val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

            buttonNext.setOnClickListener {
                if (dialogView.emailWrapper.error.isNullOrEmpty()) {
                    dialog.dismiss()
                    openDialogPasswordResetFinished()
                }
            }
        })

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            resources.getString(R.string.next),
            { _, _ -> })
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            resources.getString(R.string.cancel),
            { _, _ -> })
        dialog.show()
    }

    /**
     * dialog finished reset password
     */
    private fun openDialogPasswordResetFinished() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        dialog.setTitle(resources.getString(R.string.send_reset_email_title))
        dialog.setMessage(resources.getString(R.string.send_reset_email_message))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            resources.getString(R.string.okay),
            { _, _ -> })
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

        //permissionGranted if email is valid
        RxTextView.afterTextChangeEvents(dialogView.emailSignUpEditText)
            .skipInitialValue()
            .map {
                dialogView.emailWrapper.error = null
                it.view().text.toString()
            }
            .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(viewModel.verifyEmailPattern)
            .compose(viewModel.retryWhenError {
                dialogView.emailWrapper.error = it.message
                ViewCompat.setBackgroundTintList(
                    dialogView.emailSignUpEditText, ColorStateList
                        .valueOf(checkEditTextTintColor(it.message))
                )
            })
            .subscribe()
            .addTo(compositeDisposable)

        //permissionGranted if password is valid
        RxTextView.afterTextChangeEvents(dialogView.passwordSignUpEditText)
            .skipInitialValue()
            .map {
                dialogView.passwordWrapper.error = null
                it.view().text.toString()
            }
            .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(viewModel.lengthGreaterThanSix)
            .compose(viewModel.retryWhenError {
                dialogView.passwordWrapper.error = it.message
                ViewCompat.setBackgroundTintList(
                    dialogView.passwordSignUpEditText, ColorStateList
                        .valueOf(checkEditTextTintColor(it.message))
                )
            })
            .subscribe()
            .addTo(compositeDisposable)

        dialog.setOnShowListener({ dialog ->
            val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

            buttonNext.setOnClickListener {
                if (dialogView.agreementCheckbox.isChecked) {
                    dialog.dismiss()
                    val email = dialogView.emailSignUpEditText.text.toString()
                    val password = dialogView.passwordSignUpEditText.text.toString()
                    signup(email, password)
                } else {
                    dialogView.agreementCheckbox.setTextColor(getColor(R.color.colorAlert))
                }
            }
        })

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            resources.getString(R.string.next),
            { _, _ -> })

        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            resources.getString(R.string.cancel),
            { _, _ -> })
        dialog.show()
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password) { successful ->
            if (successful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // TODO error handling
            }
        }
    }

    private fun signup(email: String, password: String) {
        viewModel.signup(email, password) { successful ->
            if (successful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // TODO error handling
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorStatusBarSdkPre23)
        }
    }

    override fun onStop() {
        super.onStop()
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}