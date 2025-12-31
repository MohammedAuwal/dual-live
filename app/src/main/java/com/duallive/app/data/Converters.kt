package com.duallive.app.data

import androidx.room.TypeConverter
import com.duallive.app.data.entity.LeagueType

class Converters {
    @TypeConverter
    fun fromLeagueType(value: LeagueType): String = value.name

    @TypeConverter
    fun toLeagueType(value: String): LeagueType = LeagueType.valueOf(value)
}
