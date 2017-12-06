package com.example.android.themoviedb;

import android.content.Context;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FavoritePeople extends Fragment {

    private static final String TAG ="FavoritePeople" ;
    private static final int MEDIATYPE = 2 ;
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
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_people, container, false);

        ((FavoritsActivity) getActivity()).setFlipButtonListener(new FlipButtonListener() {
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
        context = getContext();
        peopleList= new ArrayList<>();
        for (String id : Utils.getMedia(context, MEDIATYPE)) {
            if (id != null && !id.isEmpty())
                id = id.trim();
            Log.v(TAG,"This is the "+id);
            new FetchMostPopular().execute(id);
        }
        return rootView;
    }



    // Todo : Using An Asynk Task instead of Retrofit:


    public class FetchMostPopular extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";
            try {
                String strUrl = "https://api.themoviedb.org/3/person/"+id+
                        "?api_key="+Utils.getApiKey()+Utils.getLanguage();
                URL url = new URL(strUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                }
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                if (stringBuffer.length() == 0) {
                    return null;
                } else {
                    strJSONMovie = stringBuffer.toString();
                    return strJSONMovie;
                }

            } catch (IOException e) {
                Log.e("Parse Error", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.isEmpty())
            fetchJSONPopular(s);
        }
        private void fetchJSONPopular (String response) {
            try{
               JSONObject jsonObject = new JSONObject(response);
               PeopleModel peopleModel= new PeopleModel();
               peopleModel.setId(jsonObject.getInt("id"));
               peopleModel.setName(jsonObject.getString("name"));
               peopleModel.setProfilePath(jsonObject.getString("profile_path"));
               peopleModel.setPopularity(jsonObject.getDouble("popularity"));
               peopleList.add(peopleModel);
                peopleAdapterBig.notifyDataSetChanged();
                peopleAdapterGrid.notifyDataSetChanged();
                peopleAdapterSmall.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }
    }
}

