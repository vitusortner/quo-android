package com.android.quo.util

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Environment
import com.android.quo.db.entity.User
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.sql.Timestamp

object QrCode {

    fun createId(user: User): String {
        val timestamp = Timestamp(System.currentTimeMillis())
        return String(Hex.encodeHex(DigestUtils.md5(timestamp.toString() + user.id)))
    }

    fun createBitmap(qrCodeId: String): Bitmap {
        val uri = Constants.QR_CODE_URI + qrCodeId
        val width = Constants.QR_CODE_DIM
        val height = Constants.QR_CODE_DIM
        val bitMatrix =
            MultiFormatWriter().encode(uri, BarcodeFormat.QR_CODE, width, height)
        val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                imageBitmap.setPixel(x, y, color)
            }
        }
        return imageBitmap
    }

    fun createFile(bitmap: Bitmap, qrCodeId: String): File {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes)

        val path = "${Environment.DIRECTORY_PICTURES}${Constants.IMAGE_DIR}"
        val storageDir = Environment.getExternalStoragePublicDirectory(path)

        if (!storageDir.exists()) storageDir.mkdirs()

        val image = File.createTempFile(qrCodeId, ".jpg", storageDir)
        FileOutputStream(image).apply {
            write(bytes.toByteArray())
            close()
        }
        return image
    }

}