package com.example.android.themoviedb.model.wrappers;

import com.example.android.themoviedb.model.PeopleModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jeomix on 11/12/17.
 */

public class PeopleModels {
    @SerializedName("results")
    List<PeopleModel> peoples;

    public List<PeopleModel> getPeoples() {
        return peoples;
    }

    public void setPeoples(List<PeopleModel> peoples) {
        this.peoples = peoples;
    }
}
