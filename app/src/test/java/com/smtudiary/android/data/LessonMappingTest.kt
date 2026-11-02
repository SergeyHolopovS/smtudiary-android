package com.smtudiary.android.data

import com.smtudiary.android.data.remote.LessonDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class LessonMappingTest {
    private val response = """
        {"name":"Математика","start":"09:00","end":"10:30","teacher":null,
         "type":"Лекция","week":"UPPER","room":"101","group":"1234","day":"Понедельник"}
    """.trimIndent()

    @Test
    fun `server fields survive conversion to entity and model`() {
        val dto = Json.decodeFromString<LessonDto>(response)
        val model = dto.toEntity().toModel()

        assertEquals(dto.start, model.start)
        assertEquals(dto.end, model.end)
        assertEquals(dto.type, model.type)
        assertEquals(dto.group, model.group)
        assertEquals(null, dto.copy(type = null).toEntity().toModel().type)
    }

    @Test
    fun `lesson identity includes separate times and type`() {
        val dto = Json.decodeFromString<LessonDto>(response)

        assertEquals(dto.toEntity().id, dto.toEntity().id)
        assertNotEquals(dto.toEntity().id, dto.copy(end = "10:45").toEntity().id)
        assertNotEquals(dto.toEntity().id, dto.copy(type = "Практика").toEntity().id)
    }
}
