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
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_qr_code_scanner.qrCodeScannerView
import kotlinx.android.synthetic.main.fragment_qr_code_scanner.flashButton
import kotlinx.android.synthetic.main.fragment_qr_code_scanner.galleryButton
import me.dm7.barcodescanner.zxing.ZXingScannerView


/**
 * Created by Jung on 30.10.17.
 */

class QrCodeScannerFragment : Fragment(), ZXingScannerView.ResultHandler {
    private val REQUEST_PERMISSION_CAMERA = 1
    private val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1
    private val RESULT_GALLERY = 0

    private var scannerView: ZXingScannerView? = null
    private var qrCodeScannerViewModel: QrCodeScannerViewModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_qr_code_scanner, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannerView = qrCodeScannerView
        scannerView!!.setAutoFocus(true)

        qrCodeScannerViewModel = ViewModelProviders.of(this).
                get(QrCodeScannerViewModel(this.activity.application)::class.java!!)

        flashButton.setOnClickListener {
            handleFlashLight()
        }

        galleryButton.setOnClickListener {
            requestPermissionGallery()
        }

        activity.bottomNavigationView.visibility = GONE
        galleryButton.background = qrCodeScannerViewModel!!.getLastImageFromGallery()
        requestPermissionCamera()
    }

    override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView?.stopCamera()
    }

    private fun startScanner() {
        scannerView!!.setResultHandler(this)
        scannerView!!.startCamera()
    }

    private fun handleFlashLight() {
        scannerView!!.flash = !scannerView!!.flash

        if (scannerView!!.flash) {
            flashButton.background = ContextCompat.getDrawable(this.context, R.drawable.ic_flash_on)
        } else {
            flashButton.background = ContextCompat.getDrawable(this.context, R.drawable.ic_flash_off)
        }
    }


    private fun requestPermissionCamera() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSION_CAMERA)
        } else {
            startScanner()
        }
    }

    private fun requestPermissionGallery() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
        } else {
            openPhoneGallery()
        }
    }

    private fun openPhoneGallery() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        ActivityCompat.startActivityForResult(this.activity, galleryIntent, RESULT_GALLERY, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanner()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity,
                    Manifest.permission.CAMERA)) {
                requestPermissionCamera()
            } else {
                AlertDialog.Builder(this.context).
                        setTitle(R.string.qr_code_camera_permission_denied_title).
                        setMessage(R.string.qr_code_camera_permission_denied_message).show()
            }
        }

    }

    override fun handleResult(p0: Result?) {
        try {
            openUrlDialogFromQRCode(qrCodeScannerViewModel!!.handleQrCode(p0))

            scannerView!!.stopCamera()
        } catch (e: Exception) {
            Log.e("Error", e.message)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            try {
                if (requestCode == RESULT_GALLERY) {
                    val selectedImageUri = data!!.data
                    val reader = MultiFormatReader()

                    val path = qrCodeScannerViewModel!!.getPath(selectedImageUri)
                    val bitmap = BitmapFactory.decodeFile(path)
                    val result = reader.decode(qrCodeScannerViewModel!!.getBinaryBitmap(bitmap))
                    val qrCode = qrCodeScannerViewModel!!.handleQrCode(result)
                    openUrlDialogFromQRCode(qrCode)
                }
            } catch (e: Exception) {
                Log.e("Error", e.message)
            }
        }
    }

    private fun openUrlDialogFromQRCode(result: QrCodeScannerDialog) {
        try {
            val urlAlert = AlertDialog.Builder(this.context).create()
            urlAlert.setTitle(resources.getString(result.title))
            urlAlert.setMessage(resources.getString(result.message) + " " + result.url)

            urlAlert.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.yes), { _, _ ->
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this.activity, Uri.parse(result.url))
            })
            urlAlert.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.no), { _, _ ->
                this.onResume()
            })
            urlAlert.show()
        } catch (e: Exception) {
            Log.e("Error", e.message)
        }
    }
}