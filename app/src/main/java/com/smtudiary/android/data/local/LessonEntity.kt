package com.smtudiary.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "lessons")
data class LessonEntity (

    @PrimaryKey
    val id: UUID,

    val name : String?,

    val time : String,

    val teacher : String?,

    val week: WeekType,

    val room : String,

    val group : String,

    val day: String,

)