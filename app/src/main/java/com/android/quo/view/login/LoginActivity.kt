package com.android.quo.view.login


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.android.quo.R
import com.android.quo.R.style.EditTextTheme
import com.android.quo.viewmodel.LoginViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView.afterTextChangeEvents
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.buttonLogin
import kotlinx.android.synthetic.main.activity_login.buttonSignUp
import kotlinx.android.synthetic.main.activity_login.editTextEmail
import kotlinx.android.synthetic.main.activity_login.editTextPassword
import kotlinx.android.synthetic.main.activity_login.emailWrapper
import kotlinx.android.synthetic.main.activity_login.passwordWrapper
import kotlinx.android.synthetic.main.activity_login.textViewClickableForgotPassword
import kotlinx.android.synthetic.main.layout_forgot_password.view.editTextEmail
import kotlinx.android.synthetic.main.layout_forgot_password.view.emailWrapper
import kotlinx.android.synthetic.main.layout_sign_up.view.agreement_checkbox
import kotlinx.android.synthetic.main.layout_sign_up.view.editTextEmailSignUp
import kotlinx.android.synthetic.main.layout_sign_up.view.editTextPasswordSignUp
import kotlinx.android.synthetic.main.layout_sign_up.view.passwordWrapper
import java.util.concurrent.TimeUnit
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.EditText
import com.android.quo.view.main.MainActivity


/**
 * Created by Jung on 09.11.17.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        callbackManager = CallbackManager.Factory.create()

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel().javaClass)


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
        afterTextChangeEvents(editTextEmail)
                .skipInitialValue()
                .map {
                    emailWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.lengthGreaterThanSix).subscribeOn(Schedulers.io())
                .compose(loginViewModel.verifyEmailPattern).subscribeOn(Schedulers.io())
                .compose(loginViewModel.retryWhenError {
                    emailWrapper.error = it.message

                    ViewCompat.setBackgroundTintList(editTextEmail, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe()

        /**
         * check if password is validate
         */
        afterTextChangeEvents(editTextPassword)
                .skipInitialValue()
                .map {
                    passwordWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.lengthGreaterThanSix).subscribeOn(Schedulers.io())
                .compose(loginViewModel.retryWhenError {
                    passwordWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(editTextEmail, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe()

        /**
         * button click handler for signIn
         */
        RxView.clicks(buttonSignUp)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { openDialogSignUp() }

        /**
         * button click handler for login button
         */
        RxView.clicks(buttonLogin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (emailWrapper.error.isNullOrEmpty() && passwordWrapper.error.isNullOrEmpty()) {
                        //TODO login user
                        
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

        /**
         * button click handler for forgot password
         */
        RxView.clicks(textViewClickableForgotPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ openDialogForgotPassword() })


    }

    /**
     * set Color to red if error
     * set Color to default if no error
     */
    private fun checkEditTextTintColor(message: String?): Int {
        var backgroundTintColor: Int = EditTextTheme
        if (!message.isNullOrEmpty()) {
            backgroundTintColor = R.color.colorErrorRed
        }
        return backgroundTintColor
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


    private fun openAlertCheckInput() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        dialog.setTitle(resources.getString(R.string.registration_failed_title))
        dialog.setMessage(resources.getString(R.string.registration_failed_message))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.ok), { _, _ -> })
        dialog.show()
    }

    /**
     * dialog forgot password
     */
    private fun openDialogForgotPassword() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        val dialogView = layoutInflater.inflate(R.layout.layout_forgot_password, null)
        dialog.setTitle(resources.getString(R.string.forgot_password))
        dialog.setView(dialogView)

        afterTextChangeEvents(dialogView.editTextEmail)
                .skipInitialValue()
                .map {
                    dialogView.emailWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.lengthGreaterThanSix).subscribeOn(Schedulers.io())
                .compose(loginViewModel.verifyEmailPattern).subscribeOn(Schedulers.io())
                .compose(loginViewModel.retryWhenError {
                    dialogView.emailWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(dialogView.editTextEmail, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe()

        dialog.setOnShowListener({ dialog ->
            val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            RxView.clicks(buttonNext)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (dialogView.emailWrapper.error.isNullOrEmpty()) {
                            dialog.dismiss()
                            openDialogPasswordResetFinished()
                        }
                    })
        })

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.next), { _, _ -> })
        dialog.show()
    }

    /**
     * dialog finished reset password
     */
    private fun openDialogPasswordResetFinished() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        dialog.setTitle(resources.getString(R.string.send_reset_email_title))
        dialog.setMessage(resources.getString(R.string.send_reset_email_message))
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

        if (editTextEmail.text.isNotEmpty()) {
            dialogView.editTextEmailSignUp.text = editTextEmail.text
        }
        if (editTextPassword.text.isNotEmpty()) {
            dialogView.editTextPasswordSignUp.text = editTextPassword.text
        }

        //check if email is validate
        afterTextChangeEvents(dialogView.editTextEmailSignUp)
                .skipInitialValue()
                .map {
                    dialogView.emailWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.lengthGreaterThanSix).subscribeOn(Schedulers.io())
                .compose(loginViewModel.verifyEmailPattern).subscribeOn(Schedulers.io())
                .compose(loginViewModel.retryWhenError {
                    dialogView.emailWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(dialogView.editTextEmailSignUp, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe()

        //check if password is validate
        afterTextChangeEvents(dialogView.editTextPasswordSignUp)
                .skipInitialValue()
                .map {
                    dialogView.passwordWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(loginViewModel.lengthGreaterThanSix).subscribeOn(Schedulers.io())
                .compose(loginViewModel.retryWhenError {
                    dialogView.passwordWrapper.error = it.message
                    ViewCompat.setBackgroundTintList(dialogView.editTextPasswordSignUp, ColorStateList
                            .valueOf(checkEditTextTintColor(it.message)))
                })
                .subscribe()

        dialog.setOnShowListener({ dialog ->
            val buttonNext = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            RxView.clicks(buttonNext)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (dialogView.agreement_checkbox.isChecked) {
                            dialog.dismiss()
                            //TODO add new account to db
                            //TODO Check if password and email have no errors
                        } else {
                            dialogView.agreement_checkbox.setTextColor(getColor(R.color.colorErrorRed))
                        }
                    }
        })

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.next), { _, _ -> })

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.cancel), { _, _ -> })
        dialog.show()
    }
}