package com.android.quo.view.qrcode

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


/**
 * Created by Jung on 30.10.17.
 */

class QrCodeScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null

    private val REQUEST_PERMISSION_CAMERA = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
    }


    override fun onBackPressed() {

        try {
            mScannerView!!.stopCamera()
        } catch (e: Exception) {
            Log.e("Error", e.message)
        }

        finish()

    }

    fun startScanner() {
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)

        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }

    fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSION_CAMERA)
        } else {
            startScanner()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanner()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                requestPermission()
            } else {
                AlertDialog.Builder(this).
                        setTitle("Take picture permission denied").
                        setMessage("You need to take picture permission. The feature will be " +
                                "disabled. To enable it, go on settings and allow the camera " +
                                "permission for the application").show()
            }
        }

    }


    override fun handleResult(p0: Result?) {
        //TODO open places page after the code is scanned
        //TODO APP closed because they can't handle the result of qr code.
        try {
            mScannerView!!.stopCamera()
            finish()
        } catch (e: Exception) {
            Log.e("Error", e.message)
        }
    }
}