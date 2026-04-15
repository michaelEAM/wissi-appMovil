package com.wissi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey val id: String,
    val nombre: String
)

