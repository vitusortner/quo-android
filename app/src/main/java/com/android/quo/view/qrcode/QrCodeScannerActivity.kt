package com.android.quo.view.qrcode

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.android.quo.R
import com.android.quo.model.QrCodeScannerDialog
import com.android.quo.viewmodel.QrCodeScannerViewModel
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_qr_code_scanner.cancelButton
import kotlinx.android.synthetic.main.activity_qr_code_scanner.flashButton
import kotlinx.android.synthetic.main.activity_qr_code_scanner.flashTextView
import kotlinx.android.synthetic.main.activity_qr_code_scanner.photosButton
import kotlinx.android.synthetic.main.activity_qr_code_scanner.qrCodeScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView


/**
 * Created by Jung on 30.10.17.
 */

class QrCodeScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1
    private val RESULT_GALLERY = 0

    private lateinit var scannerView: ZXingScannerView
    private lateinit var qrCodeScannerViewModel: QrCodeScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qr_code_scanner)

        requestPermissions(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE
        )

        // set statusbar transparent
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        supportActionBar?.hide()

        qrCodeScannerViewModel = ViewModelProviders.of(this).
                get(QrCodeScannerViewModel(application)::class.java)

        cancelButton.setOnClickListener {
            this.finish()
        }

        scannerView = qrCodeScannerView
        scannerView.setAutoFocus(true)

        flashButton.setOnClickListener {
            handleFlashLight()
        }

        photosButton.setOnClickListener {
            openPhoneGallery()
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            photosButton.background = qrCodeScannerViewModel.getLastImageFromGallery()
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    private fun handleFlashLight() {
        scannerView.flash = !scannerView.flash

        if (scannerView.flash) {
            flashButton.background = ContextCompat.getDrawable(this, R.drawable.ic_flash_on)
            flashTextView.setText(R.string.flash_off)
        } else {
            flashButton.background = ContextCompat.getDrawable(this, R.drawable.ic_flash_off)
            flashTextView.setText(R.string.flash_on)
        }
    }

    private fun openPhoneGallery() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, RESULT_GALLERY)
    }

    override fun handleResult(result: Result) {
        openUrlDialogFromQRCode(qrCodeScannerViewModel.handleQrCode(result))
        scannerView.stopCamera()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == RESULT_GALLERY) {
                    val selectedImageUri = data?.data
                    val reader = MultiFormatReader()

                    val path = selectedImageUri?.let { qrCodeScannerViewModel.getPath(it) }
                    val bitmap = BitmapFactory.decodeFile(path)

                    val result = reader.decode(qrCodeScannerViewModel.getBinaryBitmap(bitmap))
                    val qrCode = result?.let { qrCodeScannerViewModel.handleQrCode(it) }
                    qrCode?.let { openUrlDialogFromQRCode(it) }
                }
            }
        } catch (e: NotFoundException) {
            Log.e("Error", e.message.toString())
            openNoQrCodeFoundDialog()
        }
    }

    private fun openNoQrCodeFoundDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(resources.getString(R.string.error_qr_code_title))
        dialog.setMessage(resources.getString(R.string.error_qr_code_message))

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.done), { _, _ -> })
        dialog.show()
    }

    private fun openUrlDialogFromQRCode(result: QrCodeScannerDialog) {
        val urlAlert = AlertDialog.Builder(this).create()
        urlAlert.setTitle(result.title)
        urlAlert.setMessage(result.message + " " + result.url)

        urlAlert.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.open), { _, _ ->
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(result.url))
        })
        urlAlert.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.close), { _, _ ->
            this.onResume()
        })
        urlAlert.show()
    }
}