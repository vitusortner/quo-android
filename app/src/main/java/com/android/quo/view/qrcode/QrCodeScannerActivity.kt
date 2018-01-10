package com.android.quo.view.qrcode

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.android.quo.Application
import com.android.quo.R
import com.android.quo.dataclass.QrCodeScannerDialog
import com.android.quo.service.ApiService
import com.android.quo.service.SyncService
import com.android.quo.network.repository.PlaceRepository
import com.android.quo.MainActivity
import com.android.quo.viewmodel.QrCodeScannerViewModel
import com.android.quo.viewmodel.factory.QrCodeScannerViewModelFactory
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
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

    private val database = Application.database
    private val userDao = database.userDao()
    private val placeRepository = Application.placeRepository
    private val userRepository = Application.userRepository

    private lateinit var viewModel: QrCodeScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        viewModel = ViewModelProviders
                .of(this, QrCodeScannerViewModelFactory(placeRepository, userDao))
                .get(QrCodeScannerViewModel::class.java)

        requestPermissions(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE
        )

        // set statusbar transparent
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        supportActionBar?.hide()

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
            photosButton.background = getLastImageFromGallery()
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
            flashTextView.setText(R.string.qr_code_flash_off)
        } else {
            flashButton.background = ContextCompat.getDrawable(this, R.drawable.ic_flash_off)
            flashTextView.setText(R.string.qr_code_flash_on)
        }
    }

    private fun openPhoneGallery() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, RESULT_GALLERY)
    }

    /**
     * Gets called, when QR Code scanner returns result
     */
    override fun handleResult(result: Result) {
        handleQrCode(result.text)
    }

    /**
     * Opens place fragment, if supplied URI string starts with "quo://", else opens dialog
     */
    private fun handleQrCode(uriString: String) {
        Log.i("debug", "URL String: $uriString")

        when {
            uriString.startsWith("quo://") -> {
                val qrCodeId = uriString.split("/").last()

                viewModel.getPlace(qrCodeId).observe(this, Observer {
                    it?.let {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("place", it)
                        startActivity(intent)
                    }
                })
            }
            uriString.startsWith("http") -> {
                val dialog = QrCodeScannerDialog(
                        getString(R.string.qr_code_found_third_party_title),
                        getString(R.string.qr_code_found_third_party_message),
                        uriString)
                openUrlDialogFromQRCode(dialog)
            }
            else -> {
                val dialog = QrCodeScannerDialog(
                        getString(R.string.qr_code_not_found_third_party_title),
                        getString(R.string.qr_code_not_found_third_party_message),
                        uriString)
                openUrlDialogFromQRCode(dialog)
            }
        }
    }

    /**
     * Gets called, when gallery returns image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == RESULT_GALLERY) {
                    val selectedImageUri = data?.data
                    val reader = MultiFormatReader()
                    val path = selectedImageUri?.let { getPath(it) }
                    val bitmap = BitmapFactory.decodeFile(path)
                    val result = reader.decode(getBinaryBitmap(bitmap))

                    handleQrCode(result.text)
                }
            }
        } catch (e: NotFoundException) {
            Log.e("Error", e.message.toString())
            openNoQrCodeFoundDialog()
        }
    }

    private fun openNoQrCodeFoundDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(resources.getString(R.string.qr_code_error_title))
        dialog.setMessage(resources.getString(R.string.qr_code_error_message))

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.qr_code_done), { _, _ -> })
        dialog.show()
    }

    private fun openUrlDialogFromQRCode(result: QrCodeScannerDialog) {
        val urlAlert = AlertDialog.Builder(this).create()
        urlAlert.setTitle(result.title)
        urlAlert.setMessage(result.message + " " + result.url)

        urlAlert.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.fb_open), { _, _ ->
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(result.url))
        })
        urlAlert.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.fb_close), { _, _ ->
            this.onResume()
        })
        urlAlert.show()
    }

    private fun getPath(uri: Uri): String {
        var result: String? = null
        val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.contentResolver.query(uri, mediaStoreData, null, null, null)
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(mediaStoreData[0])
            result = cursor.getString(columnIndex)
        }
        cursor.close()

        return result ?: resources.getString(R.string.qr_code_not_found)
    }

    private fun getBinaryBitmap(bitmap: Bitmap): BinaryBitmap {
        val intArray = IntArray(bitmap.width * bitmap.height);
        //copy pixel data from the Bitmap into the 'intArray' array
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        return BinaryBitmap(HybridBinarizer(source))
    }

    private fun getLastImageFromGallery(): RoundedBitmapDrawable {
        val projection = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE)

        val cursor = this.contentResolver
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                if (imagePath.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    return setRoundCornerToBitmap(bitmap)
                }
            }
        }
        cursor.close()
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        return setRoundCornerToBitmap(bitmap)
    }

    private fun setRoundCornerToBitmap(bitmap: Bitmap): RoundedBitmapDrawable {
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(this.resources, bitmap)
        roundedBitmapDrawable.cornerRadius = Math.max(bitmap.width, bitmap.height) / 2.0f
        return roundedBitmapDrawable
    }
}