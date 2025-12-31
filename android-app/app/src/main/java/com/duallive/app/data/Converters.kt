package com.duallive.app.data

import androidx.room.TypeConverter
import com.duallive.app.data.entity.LeagueType

class Converters {
    @TypeConverter
    fun fromLeagueType(value: LeagueType): String {
        return value.name
    }

    @TypeConverter
    fun toLeagueType(value: String): LeagueType {
        return LeagueType.valueOf(value)
    }
}
