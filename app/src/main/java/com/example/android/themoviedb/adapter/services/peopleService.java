package com.example.android.themoviedb.adapter.services;

import com.example.android.themoviedb.model.wrappers.PeopleModels;

import retrofit.Callback;
import retrofit.http.GET;




public interface peopleService {
    @GET("/3/person/popular?api_key=d1ed76b7307ba5cec012b3685dc37dd3")
    void getPeople(Callback<PeopleModels> people);
}
