package com.ifs21035.lostfounds

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.ifs21035.lostfounds.data.remote.response.Author

class AuthorTypeConverter {

    @TypeConverter
    fun fromAuthor(author: Author): String {
        return Gson().toJson(author)
    }

    @TypeConverter
    fun toAuthor(authorJson: String): Author {
        return Gson().fromJson(authorJson, Author::class.java)
    }
}
