package com.kayodedaniel.nestnews.api

import com.kayodedaniel.nestnews.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET

interface NewsService {

    @GET("api/news")
    fun getArticles(): Call<NewsResponse>
}