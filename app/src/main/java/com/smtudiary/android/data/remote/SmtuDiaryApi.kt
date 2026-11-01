package com.smtudiary.android.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface SmtuDiaryApi {

    @GET("lessons/{group}")
    suspend fun getLessonsByGroup(@Path("group") group: String): List<LessonDto>

    @GET("groups")
    suspend fun getGroups(): List<String>

}