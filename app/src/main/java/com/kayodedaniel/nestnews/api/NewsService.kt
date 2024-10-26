package com.kayodedaniel.nestnews.api

import com.kayodedaniel.nestnews.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Defines an interface for the Retrofit service to make network requests for fetching news articles.
interface NewsService {

    // Retrieves all articles from the API.
    @GET("api/news")
    fun getArticles(): Call<NewsResponse>

    // Retrieves articles specifically under the 'business' category from the API.
    @GET("api/news?category=business")
    fun getBusinessArticles(): Call<NewsResponse>

    // Retrieves articles specifically under the 'sport' category from the API.
    @GET("api/news?category=sport")
    fun getSportsArticles(): Call<NewsResponse>

    // Retrieves articles specifically categorized as 'top stories' from the API.
    @GET("api/news?category=topstories")
    fun getTopStoriesArticles(): Call<NewsResponse>

    // Retrieves articles related to South Africa from the API.
    @GET("api/news?category=southafrica")
    fun getSouthAfricaArticles(): Call<NewsResponse>

    // Retrieves articles related to Africa from the API.
    @GET("api/news?category=africa")
    fun getAfricaArticles(): Call<NewsResponse>

    // Retrieves articles specifically related to technology ('tech') from the API.
    @GET("api/news?category=tech")
    fun getTechArticles(): Call<NewsResponse>

    // Retrieves articles related to world news from the API.
    @GET("api/news?category=world")
    fun getWorldArticles(): Call<NewsResponse>

    // Retrieves articles categorized under 'opinion' from the API.
    @GET("api/news?category=opinion")
    fun getOpinionArticles(): Call<NewsResponse>


}