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
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.model.QrCodeScannerDialog
import com.android.quo.viewmodel.QrCodeScannerViewModel
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_qr_code_scanner.flashButton
import kotlinx.android.synthetic.main.fragment_qr_code_scanner.galleryButton
import kotlinx.android.synthetic.main.fragment_qr_code_scanner.qrCodeScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView


/**
 * Created by Jung on 30.10.17.
 */

class QrCodeScannerFragment : Fragment(), ZXingScannerView.ResultHandler {
    private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1
    private val RESULT_GALLERY = 0

    private lateinit var scannerView: ZXingScannerView
    private lateinit var qrCodeScannerViewModel: QrCodeScannerViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE)

        return inflater?.inflate(R.layout.fragment_qr_code_scanner, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannerView = qrCodeScannerView
        scannerView.setAutoFocus(true)

        qrCodeScannerViewModel = ViewModelProviders.of(this).
                get(QrCodeScannerViewModel(this.activity.application)::class.java!!)


        flashButton.setOnClickListener {
            handleFlashLight()
        }

        galleryButton.setOnClickListener {
            openPhoneGallery()
        }

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            galleryButton.background = qrCodeScannerViewModel.getLastImageFromGallery()
        }

        activity.bottomNavigationView.visibility = GONE
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
            flashButton.background = ContextCompat.getDrawable(this.context, R.drawable.ic_flash_on)
        } else {
            flashButton.background = ContextCompat.getDrawable(this.context, R.drawable.ic_flash_off)
        }
    }

    private fun openPhoneGallery() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(galleryIntent, RESULT_GALLERY)
    }

    override fun handleResult(result: Result) {
        openUrlDialogFromQRCode(qrCodeScannerViewModel.handleQrCode(result))
        scannerView.stopCamera()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == RESULT_GALLERY) {
                    val selectedImageUri = data.let { data?.data }
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
        }
    }

    private fun openUrlDialogFromQRCode(result: QrCodeScannerDialog) {
        val urlAlert = AlertDialog.Builder(this.context).create()
        urlAlert.setTitle(result.title)
        urlAlert.setMessage(result.message + " " + result.url)

        urlAlert.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.open), { _, _ ->
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this.activity, Uri.parse(result.url))
        })
        urlAlert.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.close), { _, _ ->
            this.onResume()
        })
        urlAlert.show()
    }
}