package com.kayodedaniel.nestnews.api

import com.kayodedaniel.nestnews.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET

interface NewsService {

    @GET("api/news")
    fun getArticles(): Call<NewsResponse>
    @GET("api/news?category=business")
    fun getBusinessArticles(): Call<NewsResponse>
    @GET("api/news?category=sport")
    fun getSportsArticles(): Call<NewsResponse>

    @GET("api/news?category=topstories")
    fun getTopStoriesArticles(): Call<NewsResponse>

    @GET("api/news?category=southafrica")
    fun getSouthAfricaArticles(): Call<NewsResponse>
    @GET("api/news?category=africa")
    fun getAfricaArticles(): Call<NewsResponse>

    @GET("api/news?category=tech")
    fun getTechArticles(): Call<NewsResponse>

    @GET("api/news?category=world")
    fun getWorldArticles(): Call<NewsResponse>

    @GET("api/news?category=opinion")
    fun getOpinionArticles(): Call<NewsResponse>

}