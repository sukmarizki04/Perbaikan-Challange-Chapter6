package com.example.challenge6.service

import com.example.challenge6.models.Data
import com.example.challenge6.models.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TheMovieDBApiInterface {
    @GET("3/movie/popular?api_key=a97da0107371a8cbbff5148d88b92840")
    fun getPopularMovies(): Call<MovieResponse>

    @GET("3/movie/{movie_id}?api_key=a97da0107371a8cbbff5148d88b92840")
    fun getDetailsMovies(@Path("movie_id") id: Int?): Call<Data>
}