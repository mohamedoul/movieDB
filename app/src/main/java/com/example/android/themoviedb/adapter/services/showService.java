package com.example.android.themoviedb.adapter.services;

import com.example.android.themoviedb.model.wrappers.TvModels;

import retrofit.Callback;
import retrofit.http.GET;



public interface showService {

    @GET("/3/tv/popular?api_key=d1ed76b7307ba5cec012b3685dc37dd3")
    void getPopularShows(Callback<TvModels> tvModelsCallback);
}
