package com.smtudiary.android.data.remote

import com.smtudiary.android.data.local.WeekType
import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(

    val name : String?,

    val time : String,

    val teacher : String?,

    val week: WeekType,

    val room : String,

    val group : String,

    val day: String,

)
