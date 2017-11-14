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
import kotlinx.android.synthetic.main.activity_login.EmailEditText
import kotlinx.android.synthetic.main.activity_login.ErrorEmailMessage
import kotlinx.android.synthetic.main.activity_login.ErrorPasswordMessage
import kotlinx.android.synthetic.main.activity_login.PasswordEditText
import kotlinx.android.synthetic.main.activity_login.forgotPasswordClickableTextView
import kotlinx.android.synthetic.main.activity_login.signUpButton
import kotlinx.android.synthetic.main.layout_forgot_password.view.errorEmail
import kotlinx.android.synthetic.main.layout_forgot_password.view.textInputEmail
import kotlinx.android.synthetic.main.layout_sign_up.view.agreement_checkbox


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

        signUpButton.setOnClickListener {
            if (loginViewModel.verifyEmail(EmailEditText.text.toString()) && loginViewModel
                    .verifyPassword(PasswordEditText.text.toString())) {
                loginViewModel.handleRegister(EmailEditText.text.toString(),
                        PasswordEditText.text.toString())
            } else {
                openAlertCheckInput()
            }
        }

        forgotPasswordClickableTextView.setOnClickListener {
            openDialogForgotPassword()
        }

        signUpButton.setOnClickListener { openDialogSignUp() }


        /**
         * check if email is validate
         */
        EmailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (loginViewModel.verifyEmail(EmailEditText.text.toString())
                        || EmailEditText.text.isEmpty()) {
                    ErrorEmailMessage.text = ""
                    ViewCompat.setBackgroundTintList(EmailEditText, ColorStateList
                            .valueOf(EditTextTheme))
                } else {
                    ErrorEmailMessage.text = resources.getString(R.string.error_email)
                    ViewCompat.setBackgroundTintList(EmailEditText, ColorStateList
                            .valueOf(getColor(R.color.colorErrorRed)))
                }
            }
        }

        /**
         * check if password is validate
         */
        PasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (loginViewModel.verifyPassword(PasswordEditText.text.toString())
                        || PasswordEditText.text.isEmpty()) {
                    ErrorPasswordMessage.text = ""
                    ViewCompat.setBackgroundTintList(PasswordEditText, ColorStateList
                            .valueOf(EditTextTheme))
                } else {
                    ErrorPasswordMessage.text = resources.getString(R.string.error_password)
                    ViewCompat.setBackgroundTintList(PasswordEditText, ColorStateList
                            .valueOf(getColor(R.color.colorErrorRed)))
                }
            }
        }

        /**
         * Handle Facebook result
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

    private fun openDialogForgotPassword() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        val dialogView = layoutInflater.inflate(R.layout.layout_forgot_password, null)
        dialog.setTitle(resources.getString(R.string.forgot_password))
        dialog.setView(dialogView)

        dialog.setOnShowListener({ dialog ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if (loginViewModel.verifyEmail(dialogView.textInputEmail.text.toString())) {
                    dialogView.errorEmail.text = ""
                    ViewCompat.setBackgroundTintList(dialogView.textInputEmail, ColorStateList
                            .valueOf(EditTextTheme))
                    dialog.dismiss()
                    openDialogPasswordResetFinished()
                } else {
                    dialogView.errorEmail.text = resources.getString(R.string.error_email)
                    ViewCompat.setBackgroundTintList(dialogView.textInputEmail, ColorStateList
                            .valueOf(getColor(R.color.colorErrorRed)))
                }
            }
        })

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.next), { _, _ ->
            if (loginViewModel.sendEmailToUser(dialogView.textInputEmail.text.toString())) {
            }
        })
        dialog.show()
    }

    private fun openDialogPasswordResetFinished() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        dialog.setTitle(resources.getString(R.string.send_reset_email_title))
        dialog.setMessage(resources.getString(R.string.send_reset_email_message))
        dialog.show()
    }

    private fun openDialogSignUp() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme).create()
        val dialogView = layoutInflater.inflate(R.layout.layout_sign_up, null)
        dialog.setTitle(resources.getString(R.string.sign_up))
        dialog.setView(dialogView)

        dialog.setOnShowListener({ dialog ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if (dialogView.agreement_checkbox.isActivated) {
                    dialog.dismiss()
                } else {
                    dialogView.agreement_checkbox.setTextColor(getColor(R.color.colorErrorRed))
                }
            }
        })

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.next), { _, _ ->

        })

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.cancel), { _, _ -> })
        dialog.show()
    }
}