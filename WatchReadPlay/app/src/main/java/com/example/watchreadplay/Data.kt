package com.example.watchreadplay

import android.graphics.drawable.Drawable

data class Data(
    val id: String = "",
    val type: String? = "",
    val title: String = "",
    val original_title: String = "",
    val release_date: Int = 0,
    val author: String = "",
    val completion_date: String = "-",
    var isClicked: Boolean = false,
    var isLongClicked: Boolean = false,
    var icon: Drawable? = null)