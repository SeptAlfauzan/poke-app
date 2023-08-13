package com.example.poke.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun getTwoDominantColors(
    imageUrl: String,
    context: Context,
    updateState: (color: Int) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .target {drawable ->
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

                    Palette.from(resizedBitmap).generate() { pallet ->
                        pallet?.let {
                            val vibrant = it.getVibrantColor(0x000000)
                            val darkVibrant = it.getDarkVibrantColor(0x000000)
                            val dominantColor = it.getDominantColor(0x001212)
                            updateState(dominantColor)
                        }
                    }
                }
                .build()
            imageLoader.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}