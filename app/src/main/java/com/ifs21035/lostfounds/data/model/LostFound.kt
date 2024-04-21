package com.ifs21035.lostfounds.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class LostFound(
    val id: Int,
    val title: String,
    val description: String,
    var status: String,
    var isCompleted: Boolean,
    val cover: String?,
) : Parcelable