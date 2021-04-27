package com.example.watchreadplay

import android.graphics.drawable.Drawable
import com.google.firebase.database.Exclude

data class Data(
    val id: String = "",
    val type: String? = "",
    val title: String = "",
    val original_title: String = "",
    val release_date: Int = 0,
    val author: String = "",
    var completion_date: String = "-",
    @Exclude @set:Exclude @get:Exclude var isClicked: Boolean = false,
    @Exclude @set:Exclude @get:Exclude var isLongClicked: Boolean = false,
    @Exclude @set:Exclude @get:Exclude var icon: Drawable? = null)