package com.smtudiary.android.data.model

import com.smtudiary.android.data.local.WeekType
import java.util.UUID

data class Lesson(

    val id: UUID,

    val name : String?,

    val time : String,

    val teacher : String?,

    val week: WeekType,

    val room : String,

    val group : String,

    val day: String,

)
