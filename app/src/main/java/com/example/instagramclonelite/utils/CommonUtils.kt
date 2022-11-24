package com.example.instagramclonelite.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class CommonUtils {
    companion object{
        fun bitmapToByteArray(bitmap: Bitmap, imageSize: Int): ByteArray {
            val newBitmap = ImageResizer.generateThumb(bitmap, imageSize)
            val stream = ByteArrayOutputStream()
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)

            return stream.toByteArray()
        }
    }
}