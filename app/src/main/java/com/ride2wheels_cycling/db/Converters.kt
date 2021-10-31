package com.ride2wheels_cycling.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    //bájt-tömbök képpé konvertálása
    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes,0, bytes.size)
    }

    //képek konvertálása bájt-tömbökbe
    @TypeConverter
    fun fromBitmap(bmp: Bitmap) : ByteArray {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}