package com.anorisno.takephoto.tool

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotateBitmap(rotaitionDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(-rotaitionDegrees.toFloat())
//        postScale(-1f, -1f)
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}