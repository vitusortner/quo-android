package com.android.quo.view.qrcode

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.WindowManager
import com.android.quo.MainActivity
import com.android.quo.R
import com.android.quo.dataclass.QrCodeScannerDialog
import com.android.quo.db.entity.Place
import com.android.quo.util.Constants
import com.android.quo.util.Constants.Extra
import com.android.quo.util.Constants.HTTP
import com.android.quo.util.Constants.QR_CODE_URI
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_GPS
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_MULTIPLE
import com.android.quo.util.Constants.Request.REQUEST_GALLERY
import com.android.quo.util.extension.getImagePath
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.view.BaseActivity
import com.android.quo.viewmodel.QrCodeScannerViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import kotlinx.android.synthetic.main.activity_qr_code_scanner.cancelButton
import kotlinx.android.synthetic.main.activity_qr_code_scanner.flashButton
import kotlinx.android.synthetic.main.activity_qr_code_scanner.flashTextView
import kotlinx.android.synthetic.main.activity_qr_code_scanner.photosButton
import kotlinx.android.synthetic.main.activity_qr_code_scanner.qrCodeScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.architecture.ext.viewModel

/**
 * Created by Jung on 30.10.17.
 */
// TODO too big!
class QrCodeScannerActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    private val viewModel by viewModel<QrCodeScannerViewModel>()

    private lateinit var scannerView: ZXingScannerView
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_MULTIPLE
        )

        // set statusbar transparent
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )

        supportActionBar?.hide()

        cancelButton.setOnClickListener { finish() }

        scannerView = qrCodeScannerView
        scannerView.setAutoFocus(true)

        flashButton.setOnClickListener { handleFlashLight() }

        photosButton.setOnClickListener { openPhoneGallery() }

        permissionsGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
            .takeIf { it }
            ?.run { photosButton.background = getLastImageFromGallery() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_MULTIPLE -> trySetupLocationClient()
            PERMISSION_REQUEST_GPS -> trySetupLocationClient()

        }
    }

    private fun trySetupLocationClient() =
        permissionsGranted(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
            .takeIf { it }
            ?.let { locationClient = LocationServices.getFusedLocationProviderClient(this) }

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
            flashTextView.setText(R.string.qr_code_flash_on)
        } else {
            flashButton.background = ContextCompat.getDrawable(this, R.drawable.ic_flash_off)
            flashTextView.setText(R.string.qr_code_flash_off)
        }
    }

    private fun openPhoneGallery() =
        Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            .let { startActivityForResult(it, REQUEST_GALLERY) }

    /**
     * Gets called, when QR Code scanner returns result
     */
    override fun handleResult(result: Result) =
        handleQrCodeUri(result.text)

    /**
     * Opens place fragment, if supplied URI string starts with "quo://", else opens dialog
     */
    private fun handleQrCodeUri(uriString: String) {
        log.i("URI String: $uriString")

        when {
            uriString.startsWith(QR_CODE_URI) -> {
                val qrCodeId = uriString.split("/").last()
                this
                    .permissionsGranted(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .let { if (it) tryOpenPlace(qrCodeId) else openLocationOffAlert() }
            }
            uriString.startsWith(HTTP) -> {
                val dialog = QrCodeScannerDialog(
                    getString(R.string.qr_code_found_third_party_title),
                    getString(R.string.qr_code_found_third_party_message),
                    uriString
                )
                openUrlDialogFromQRCode(dialog)
            }
            else -> {
                val dialog = QrCodeScannerDialog(
                    getString(R.string.qr_code_not_found_third_party_title),
                    getString(R.string.qr_code_not_found_third_party_message),
                    uriString
                )
                openUrlDialogFromQRCode(dialog)
            }
        }
    }

    private fun openLocationOffAlert() {
        val alert = AlertDialog.Builder(this).create()

        alert.setTitle(getString(R.string.qr_code_location_off_title))
        alert.setMessage(getString(R.string.qr_code_location_off_message))

        alert.setButton(AlertDialog.BUTTON_POSITIVE,
            getString(R.string.qr_code_location_off_turn_on), { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSION_REQUEST_GPS
                )
            })
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.qr_code_location_off_no), { _, _ ->
                onResume()
            })

        alert.show()
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    @SuppressLint("MissingPermission")
    private fun tryOpenPlace(qrCodeId: String) {
        viewModel.getPlace(qrCodeId).observe(this, Observer {
            it?.let { place ->
                // TODO this is just a dirty fix, better not use live data in this case
                viewModel.resetLiveData()

                // TODO remove nullability of hasToValidateGps
                place.hasToValidateGps?.let { hasToValidateGps ->
                    if (hasToValidateGps) {
                        locationClient.lastLocation.addOnSuccessListener {
                            it?.let {
                                // Create location object from place lat and long
                                val placeLocation = Location("")
                                placeLocation.latitude = place.latitude
                                placeLocation.longitude = place.longitude

                                if (placeLocation.distanceTo(it) <= Constants.LOCATION_DISTANCE
                                    || place.isHost
                                ) {
                                    startPlaceIntent(place)
                                } else {
                                    openWrongLocationAlert()
                                }
                            } ?: openLocationErrorAlert()
                        }
                    } else {
                        startPlaceIntent(place)
                    }
                }
            }
        })
    }

    private fun openLocationErrorAlert() {
        val alert = AlertDialog.Builder(this).create()

        alert.setTitle(getString(R.string.qr_core_location_error_title))
        alert.setMessage(getString(R.string.qr_code_location_error_message))

        alert.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.qr_code_location_error_ok),
            { _, _ ->
                onResume()
            })

        alert.show()
    }

    private fun openWrongLocationAlert() {
        // show alert - user is too far away from place
        val alert = AlertDialog.Builder(this).create()

        alert.setTitle(getString(R.string.qr_code_wrong_location_title))
        alert.setMessage(getString(R.string.qr_code_wrong_location_message))

        alert.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.qr_code_wrong_location_ok),
            { _, _ ->
                onResume()
            })

        alert.show()
    }

    private fun startPlaceIntent(place: Place) =
        Intent(this, MainActivity::class.java).apply {
            putExtra(Extra.PLACE_EXTRA, place)
            startActivity(this)
        }

    /**
     * Gets called, when gallery returns image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    REQUEST_GALLERY -> {
                        data?.data?.getImagePath(this)?.let {
                            val bitmap = BitmapFactory.decodeFile(it)
                            val result = MultiFormatReader().decode(getBinaryBitmap(bitmap))
                            handleQrCodeUri(result.text)
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            log.e("Error while getting image from gallery.", exception)
            openNoQrCodeFoundDialog()
        }
    }

    private fun openNoQrCodeFoundDialog() {
        val dialog = AlertDialog.Builder(this).create()

        dialog.setTitle(resources.getString(R.string.qr_code_error_title))
        dialog.setMessage(resources.getString(R.string.qr_code_error_message))

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            resources.getString(R.string.qr_code_done),
            { _, _ -> })
        dialog.show()
    }

    private fun openUrlDialogFromQRCode(result: QrCodeScannerDialog) {
        val urlAlert = AlertDialog.Builder(this).create()

        urlAlert.setTitle(result.title)
        urlAlert.setMessage("${result.message} ${result.url}")

        urlAlert.setButton(
            AlertDialog.BUTTON_POSITIVE,
            resources.getString(R.string.fb_open),
            { _, _ ->
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(result.url))
            })
        urlAlert.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            resources.getString(R.string.fb_close),
            { _, _ ->
                onResume()
            })

        urlAlert.show()
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
            MediaStore.Images.ImageColumns.MIME_TYPE
        )

        contentResolver
            .query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
            )
            ?.apply {
                while (moveToNext()) {
                    val imagePath = getString(getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                    if (imagePath.isNotEmpty()) {
                        close()
                        val bitmap = BitmapFactory.decodeFile(imagePath)
                        return setRoundCornerToBitmap(bitmap)
                    }
                }
            }

        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        return setRoundCornerToBitmap(bitmap)
    }

    private fun setRoundCornerToBitmap(bitmap: Bitmap): RoundedBitmapDrawable =
        RoundedBitmapDrawableFactory.create(resources, bitmap).apply {
            cornerRadius = Math.max(bitmap.width, bitmap.height) / 2.0f
        }

}