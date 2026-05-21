package com.wissi.data.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Product(

    val id: Int? = null,

    val name: String = "",

    val code: String = "",

    val description: String? = null,

    val price: Double = 0.0,

    val stock: Int = 0,

    @SerialName("category_id")
    val categoryId: Int? = null,

    @SerialName("image_url")
    val imageUrl: String? = null,

    val ubicacion: String? = null,

    @SerialName("stock_bodega")
    val stockBodega: Int? = 0,

    @SerialName("stock_tienda")
    val stockTienda: Int? = 0,

    val created_at: String? = null
)


@Serializable
data class Category(

    val id: Int? = null,

    val name: String
)
