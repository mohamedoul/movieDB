package com.example.android.themoviedb;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.example.android.themoviedb.adapter.PeopleAdapter;
import com.example.android.themoviedb.adapter.TypeShow;
import com.example.android.themoviedb.adapter.services.peopleService;
import com.example.android.themoviedb.listener.FlipButtonListener;
import com.example.android.themoviedb.model.PeopleModel;
import com.example.android.themoviedb.model.wrappers.PeopleModels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class Tab2People extends Fragment {

    private PeopleAdapter peopleAdapterBig;
    private PeopleAdapter peopleAdapterGrid;
    private PeopleAdapter peopleAdapterSmall;
    private ProgressBar progressBar;
    private List<PeopleModel> peopleList = new ArrayList<>();
    private ViewFlipper viewFlipper;
    private RecyclerView smallRecycleView;
    private RecyclerView gridRecycleView;
    private RecyclerView bigRecycleView;
    private StaggeredGridLayoutManager gridLayoutManager;
    private LinearLayoutManager bigLayoutManager;
    private LinearLayoutManager smallLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_people, container, false);

        ((MainActivity) getActivity()).setFlipButtonListener(new FlipButtonListener() {
            @Override
            public void flip() {
                bigRecycleView.setSelected(false);
                gridRecycleView.setSelected(false);
                smallRecycleView.setSelected(false);
                viewFlipper.showNext();
            }
        });

        viewFlipper = ((ViewFlipper)rootView.findViewById(R.id.view_flipper3));
        viewFlipper.setOutAnimation(rootView.getContext(), R.anim.anim_fade_out_flip);
        viewFlipper.setInAnimation(rootView.getContext(), R.anim.anim_fade_in_flip);

        bigRecycleView = ((RecyclerView)rootView.findViewById(R.id.big_list));
        gridRecycleView = ((RecyclerView)rootView.findViewById(R.id.grid_list));
        smallRecycleView = ((RecyclerView)rootView.findViewById(R.id.smallList));

//GRID VIEW
        peopleAdapterGrid=new PeopleAdapter(getActivity(), peopleList, TypeShow.Grid);
        gridLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridRecycleView.setLayoutManager(gridLayoutManager);
        gridRecycleView.setNestedScrollingEnabled(false);
        gridRecycleView.setAdapter(peopleAdapterGrid);
        gridRecycleView.setFocusable(false);

//BIG LIST VIEW
        peopleAdapterBig = new PeopleAdapter(getActivity(), peopleList);
        bigLayoutManager = new LinearLayoutManager(getActivity());
        bigRecycleView.setLayoutManager(bigLayoutManager);
        bigRecycleView.setNestedScrollingEnabled(false);
        bigRecycleView.setAdapter(peopleAdapterBig);
        bigRecycleView.setFocusable(false);

//SMALL LIST VIEW
        peopleAdapterSmall=new PeopleAdapter(getActivity(), peopleList, TypeShow.Small);
        smallLayoutManager=  new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        smallRecycleView.setLayoutManager(smallLayoutManager);
        smallRecycleView.setNestedScrollingEnabled(false);
        smallRecycleView.setAdapter(peopleAdapterSmall);
        smallRecycleView.setFocusable(false);

        if (Build.VERSION.SDK_INT >= 11)
        {
            bigRecycleView.setVerticalScrollbarPosition(1);
            gridRecycleView.setVerticalScrollbarPosition(1);
            smallRecycleView.setVerticalScrollbarPosition(1);
        }

        try {
            retrofitMethod();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public void retrofitMethod() throws IOException{
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint("https://api.themoviedb.org")
                .build();
        peopleService peopleService = retrofit.create(com.example.android.themoviedb.adapter.services.peopleService.class);
        peopleService.getPeople(new Callback<PeopleModels>() {
            @Override
            public void success(PeopleModels peopleModels, Response response) {
                peopleList.addAll(peopleModels.getPeoples().subList(0,19));
                peopleAdapterBig.notifyDataSetChanged();
                Log.v("Response received",response.getBody().toString());
            }
            @Override
            public void failure(RetrofitError error) {
                Log.v("fail",error.getUrl() + " + " + error.getMessage());
            }
        });
    }



}

