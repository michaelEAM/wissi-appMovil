package com.wissi.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    
    private const val IMAGES_DIR = "images"
    
    /**
     * Guarda imagen seleccionada en almacenamiento interno de la app
     * @return Ruta del archivo guardado (ej: "producto_abc123.jpg") o null si error
     */
    suspend fun saveImageToInternalStorage(
        context: Context, 
        uri: Uri, 
        filename: String
    ): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            // Decodificar bitmap de URI
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap == null) return@withContext null
            
            // Crear directorio images/
            val imagesDir = File(context.filesDir, IMAGES_DIR)
            if (!imagesDir.exists()) imagesDir.mkdirs()
            
            // Crear archivo
            val imageFile = File(imagesDir, filename)
            
            // Comprimir y guardar como JPEG
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            
            bitmap.recycle()
            
            // Retornar nombre del archivo (persistente)
            imageFile.name
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Genera URI file:// para mostrar imagen guardada
     */
    fun getImageUri(context: Context, imagePath: String): Uri {
        val imagesDir = File(context.filesDir, IMAGES_DIR)
        val imageFile = File(imagesDir, imagePath)
        return Uri.fromFile(imageFile)
    }
    
    /**
     * Crea nombre único para imagen de producto
     */
    fun createUniqueImageFilename(productId: String): String {
        return "producto_${productId}.jpg"
    }
}

