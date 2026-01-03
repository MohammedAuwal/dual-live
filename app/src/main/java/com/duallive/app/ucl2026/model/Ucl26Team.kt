package com.duallive.app.ucl2026.model

data class Ucl26Team(
    val id: Int,
    val name: String,
    val logoRes: Int? = null,
    val pot: Int // 1, 2, 3, or 4
)
