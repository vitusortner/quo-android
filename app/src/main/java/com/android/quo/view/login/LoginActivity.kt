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
import kotlinx.android.synthetic.main.activity_login.forgotPasswordClickableTextView
import kotlinx.android.synthetic.main.activity_login.loginEmailEditText
import kotlinx.android.synthetic.main.activity_login.loginErrorEmailMessage
import kotlinx.android.synthetic.main.activity_login.loginErrorPasswordMessage
import kotlinx.android.synthetic.main.activity_login.loginPasswordEditText
import kotlinx.android.synthetic.main.activity_login.signUpButton
import kotlinx.android.synthetic.main.layout_forgot_password.view.errorEmail
import kotlinx.android.synthetic.main.layout_forgot_password.view.textInputEmail


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
            if (loginViewModel.verifyEmail(loginEmailEditText.text.toString()) && loginViewModel
                    .verifyPassword(loginPasswordEditText.text.toString())) {
                loginViewModel.handleRegister(loginEmailEditText.text.toString(),
                        loginPasswordEditText.text.toString())
            } else {
                openAlertCheckInput()
            }
        }

        forgotPasswordClickableTextView.setOnClickListener {
            openDialogForgotPassword()
        }


        loginEmailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (loginViewModel.verifyEmail(loginEmailEditText.text.toString())) {
                    loginErrorEmailMessage.text = ""
                    ViewCompat.setBackgroundTintList(loginEmailEditText, ColorStateList
                            .valueOf(EditTextTheme))
                } else {
                    loginErrorEmailMessage.text = resources.getString(R.string.error_email)
                    ViewCompat.setBackgroundTintList(loginEmailEditText, ColorStateList
                            .valueOf(getColor(R.color.colorErrorRed)))
                }
            }
        }

        loginPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (loginViewModel.verifyPassword(loginPasswordEditText.text.toString())) {
                    loginErrorPasswordMessage.text = ""
                    ViewCompat.setBackgroundTintList(loginPasswordEditText, ColorStateList
                            .valueOf(EditTextTheme))
                } else {
                    loginErrorPasswordMessage.text = resources.getString(R.string.error_password)
                    ViewCompat.setBackgroundTintList(loginPasswordEditText, ColorStateList
                            .valueOf(getColor(R.color.colorErrorRed)))
                }
            }
        }

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
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(resources.getString(R.string.registration_failed_title))
        dialog.setMessage(resources.getString(R.string.registration_failed_message))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.ok), { _, _ -> })
        dialog.show()
    }

    private fun openDialogForgotPassword() {
        val dialog = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.layout_forgot_password, null)
        dialog.setTitle(resources.getString(R.string.forgot_password))
        dialog.setView(dialogView)

        dialogView.textInputEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (loginViewModel.verifyEmail(loginEmailEditText.text.toString())) {
                    loginErrorEmailMessage.text = ""
                } else {
                    loginErrorEmailMessage.text = resources.getString(R.string.error_email)
                }
            }
        }

        dialog.setOnShowListener({ dialog ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if (loginViewModel.verifyEmail(dialogView.textInputEmail.text.toString())) {
                    dialogView.errorEmail.text = ""
                    dialog.dismiss()
                    openDialogPasswordResetFinished()
                } else {
                    dialogView.errorEmail.text = resources.getString(R.string.error_email)
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
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(resources.getString(R.string.send_reset_email_title))
        dialog.setMessage(resources.getString(R.string.send_reset_email_message))
        dialog.show()
    }
}