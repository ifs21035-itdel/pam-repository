package com.ifs21035.lostfounds.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ifs21035.lostfounds.AuthorTypeConverter
import com.ifs21035.lostfounds.data.remote.response.Author
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "lost_founds")
@TypeConverters(AuthorTypeConverter::class)
data class LostFoundEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "is_completed")
    var isCompleted: Int,

    @ColumnInfo(name = "cover")
    var cover: String?,

    @ColumnInfo(name = "created_at")
    var createdAt: String,

    @ColumnInfo(name = "updated_at")
    var updatedAt: String,

    @ColumnInfo(name = "status")
    var status: String,

    @ColumnInfo(name = "author")
    var author: @RawValue Author?,

    @ColumnInfo(name = "user_id")
    var userId: Int,
) : Parcelable
