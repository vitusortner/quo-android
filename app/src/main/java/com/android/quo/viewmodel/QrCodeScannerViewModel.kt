package com.android.quo.viewmodel


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Log
import com.android.quo.R
import com.android.quo.model.QrCodeScannerDialog
import com.google.zxing.BinaryBitmap
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer

/**
 * Created by Jung on 30.10.17.
 */


class QrCodeScannerViewModel(application: Application) : AndroidViewModel(application) {

    private var context: Context? = null
    private var projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE)

    init {
        context = application.applicationContext
    }

    fun handleQrCode(url: Result?): QrCodeScannerDialog {
        try {
            if (url.toString().contains("http")) {
                return QrCodeScannerDialog(
                        R.string.qr_code_found_third_party_title,
                        R.string.qr_code_found_third_party_message,
                        url.toString())
            } else {
                //TODO open places page after the code is scanned
            }

        } catch (e: NotFoundException) {
            Log.e("Error", e.message)
        }
        return return QrCodeScannerDialog(
                R.string.qr_code_not_found_third_party_title,
                R.string.qr_code_not_found_third_party_message,
                url.toString())
    }

    fun getPath(uri: Uri): String {
        var result: String? = null
        val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context!!.contentResolver.query(uri, mediaStoreData, null, null, null)
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor!!.getColumnIndexOrThrow(mediaStoreData[0])
                result = cursor!!.getString(columnIndex)
            }
            cursor!!.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    fun getBinaryBitmap(bitmap: Bitmap): BinaryBitmap {
        val intArray = IntArray(bitmap.width * bitmap.height);
        //copy pixel data from the Bitmap into the 'intArray' array
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        return BinaryBitmap(HybridBinarizer(source))
    }

    fun getLastImageFromGallery(): RoundedBitmapDrawable {
        val cursor = context!!.contentResolver
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")

        while (cursor.moveToNext()) {
            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
            if (imagePath.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                return setRoundCornerToBitmap(bitmap)
                break
            }
        }
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        return setRoundCornerToBitmap(bitmap)
    }

    private fun setRoundCornerToBitmap(bitmap: Bitmap): RoundedBitmapDrawable {
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context!!.resources, bitmap)
        roundedBitmapDrawable.cornerRadius = Math.max(bitmap.width, bitmap.height) / 2.0f
        return roundedBitmapDrawable
    }
}