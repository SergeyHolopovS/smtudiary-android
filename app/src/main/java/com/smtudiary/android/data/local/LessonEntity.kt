package com.smtudiary.android.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["group"],
            childColumns = ["group"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("group")]
)
data class LessonEntity (

    @PrimaryKey
    val id: UUID,

    val name : String?,

    val time : String,

    val teacher : String?,

    val week: WeekType,

    val room : String,

    val group : Int,

    val day: String,

)