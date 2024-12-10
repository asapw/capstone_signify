package com.example.mycapstone.utils

import android.content.Context
import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

object ImageConvert {
    fun bitmapToMultipart(bitmap: Bitmap, fieldName: String = "file"): MultipartBody.Part {
        // Create a temporary file
        val tempFile = File.createTempFile("temp_image", ".jpeg")

        // Convert the Bitmap to a ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Write the ByteArray to the temporary file
        tempFile.writeBytes(byteArray)

        // Create a RequestBody from the temporary file
        val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

        // Return MultipartBody.Part
        return MultipartBody.Part.createFormData(fieldName, tempFile.name, requestBody)
    }
}