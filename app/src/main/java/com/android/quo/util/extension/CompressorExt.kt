package com.android.quo.util.extension

import android.graphics.Bitmap
import id.zelory.compressor.Compressor
import java.io.File

/**
 * Created by vitusortner on 28.02.18.
 */
fun Compressor.compressImage(
    image: File,
    width: Int,
    height: Int,
    quality: Int,
    dirPath: String? = null
) =
    this
        .setMaxWidth(width)
        .setMaxHeight(height)
        .setQuality(quality)
        .setCompressFormat(Bitmap.CompressFormat.JPEG)
        .apply { dirPath?.let { setDestinationDirectoryPath(it) } }
        .compressToFileAsFlowable(image)