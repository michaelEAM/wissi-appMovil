package com.wissi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey val id: String = "",
    val nombre: String,
    val codigo: String,
    @ColumnInfo(name = "categoria_id") val categoriaId: String,
    val precio: Double,
    val cantidad: Int,
    val imagenPath: String? = null,
    val imagenResId: Int
)
