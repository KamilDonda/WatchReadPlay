package com.example.watchreadplay

import android.graphics.drawable.Drawable

data class Data(
    val type: String,
    val title: String,
    val original_title: String,
    val release_date: Int,
    val author: String,
    val completion_date: String,
    var isClicked: Boolean,
    val icon: Drawable? = null)