package com.wissi.data.supabase


import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.storage
import java.util.UUID


object SupabaseStorageManager {

    suspend fun uploadImage(
        context: Context,
        uri: Uri
    ): String {

        val inputStream =
            context.contentResolver.openInputStream(uri)

        val bytes = inputStream?.readBytes()
            ?: throw Exception("No se pudo leer imagen")

        // SOLO EL NOMBRE
        val fileName = "${UUID.randomUUID()}.jpg"

        SupabaseClientProvider.client
            .storage
            .from("productos")
            .upload(
                path = fileName,
                data = bytes
            )

        return SupabaseClientProvider.client
            .storage
            .from("productos")
            .publicUrl(fileName)
    }
}