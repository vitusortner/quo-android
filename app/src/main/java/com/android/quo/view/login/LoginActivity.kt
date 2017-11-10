package com.android.quo.view.login

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.android.quo.R
import com.android.quo.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.forgotPasswordClickableTextView
import kotlinx.android.synthetic.main.activity_login.loginEmailEditText
import kotlinx.android.synthetic.main.activity_login.loginErrorEmailMessage
import kotlinx.android.synthetic.main.activity_login.loginErrorPasswordMessage
import kotlinx.android.synthetic.main.activity_login.loginPasswordEditText
import kotlinx.android.synthetic.main.activity_login.registerButton
import kotlinx.android.synthetic.main.layout_forgot_password.view.errorEmail
import kotlinx.android.synthetic.main.layout_forgot_password.view.textInputEmail


/**
 * Created by Jung on 09.11.17.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel().javaClass)

        registerButton.setOnClickListener {
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

        loginEmailEditText.afterTextChanged {
            if (loginViewModel.verifyEmail(loginEmailEditText.text.toString())) {
                loginErrorEmailMessage.text = ""
            } else {
                loginErrorEmailMessage.text = resources.getString(R.string.error_email)
            }
        }

        loginPasswordEditText.afterTextChanged {
            if (loginViewModel.verifyPassword(loginPasswordEditText.text.toString())) {
                loginErrorPasswordMessage.text = ""
            } else {
                loginErrorPasswordMessage.text = resources.getString(R.string.error_password)
            }
        }
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

        dialogView.textInputEmail.afterTextChanged {
            if (loginViewModel.verifyEmail(loginEmailEditText.text.toString())) {
                loginErrorEmailMessage.text = ""
            } else {
                loginErrorEmailMessage.text = resources.getString(R.string.error_email)
            }
        }

        dialog.setOnShowListener({ dialog ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if (loginViewModel.verifyEmail(dialogView.textInputEmail.text.toString())) {
                    dialogView.errorEmail.text = ""
                    dialog.dismiss()
                } else {
                    dialogView.errorEmail.text = resources.getString(R.string.error_email)
                }
            }
        })

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.next), { _, _ ->
            if (loginViewModel.sendEmailToUser(dialogView.textInputEmail.text.toString())) {
                openDialogPasswordResetFinished()
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

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }
}