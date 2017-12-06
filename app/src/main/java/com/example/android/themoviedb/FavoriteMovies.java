package com.example.android.themoviedb;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.example.android.themoviedb.adapter.MovieAdapter;
import com.example.android.themoviedb.adapter.TypeShow;
import com.example.android.themoviedb.listener.FlipButtonListener;
import com.example.android.themoviedb.model.GenreModel;
import com.example.android.themoviedb.model.MovieModel;

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


public class FavoriteMovies extends Fragment {

    private MovieAdapter movieAdapterSmall;
    private MovieAdapter movieAdapterGrid;
    private MovieAdapter movieAdapterBig;
    private List<GenreModel> genreList = new ArrayList<GenreModel>();
    private List<MovieModel> movieList = new ArrayList<MovieModel>();
    private List<String> movieGenreNames;
    private ProgressBar progressBar;
    private final String TAG="FAVORITE_MOVIES";
    private final int MEDIATYPE=0;
    private Context context;
    private ViewFlipper viewFlipper;
    private RecyclerView smallRecycleView;
    private RecyclerView gridRecycleView;
    private RecyclerView bigRecycleView;
    private StaggeredGridLayoutManager gridLayoutManager;
    private LinearLayoutManager bigLayoutManager;
    private LinearLayoutManager smallLayoutManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View rootView= inflater.inflate(R.layout.favorite_movies, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        rootView.setTag(TAG);
        ((FavoritsActivity) getActivity()).setFlipButtonListener(new FlipButtonListener() {
            @Override
            public void flip() {
                bigRecycleView.setSelected(false);
                gridRecycleView.setSelected(false);
                smallRecycleView.setSelected(false);
                viewFlipper.showNext();
            }
        });
        viewFlipper = ((ViewFlipper)rootView.findViewById(R.id.view_flipper));
        viewFlipper.setOutAnimation(rootView.getContext(), R.anim.anim_fade_out_flip);
        viewFlipper.setInAnimation(rootView.getContext(), R.anim.anim_fade_in_flip);

        bigRecycleView = ((RecyclerView)rootView.findViewById(R.id.big_list));
        gridRecycleView = ((RecyclerView)rootView.findViewById(R.id.grid_list));
        smallRecycleView = ((RecyclerView)rootView.findViewById(R.id.smallList));
//GRID VIEW
        movieAdapterGrid=new MovieAdapter(getActivity(), movieList, TypeShow.Grid);
        gridLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridRecycleView.setLayoutManager(gridLayoutManager);
        gridRecycleView.setNestedScrollingEnabled(false);
        gridRecycleView.setAdapter(movieAdapterGrid);
        gridRecycleView.setFocusable(false);
//BIG LIST VIEW
        movieAdapterBig = new MovieAdapter(getActivity(), movieList, TypeShow.Big);
        bigLayoutManager = new LinearLayoutManager(getActivity());
        bigRecycleView.setLayoutManager(bigLayoutManager);
        bigRecycleView.setNestedScrollingEnabled(false);
        bigRecycleView.setAdapter(movieAdapterBig);
        bigRecycleView.setFocusable(false);
//SMALL LIST VIEW
        movieAdapterSmall=new MovieAdapter(getActivity(),movieList, TypeShow.Small);
        smallLayoutManager=  new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        smallRecycleView.setLayoutManager(smallLayoutManager);
        smallRecycleView.setNestedScrollingEnabled(false);
        smallRecycleView.setAdapter(movieAdapterSmall);
        smallRecycleView.setFocusable(false);


        if (Build.VERSION.SDK_INT >= 11)
        {
            bigRecycleView.setVerticalScrollbarPosition(1);
            gridRecycleView.setVerticalScrollbarPosition(1);
            smallRecycleView.setVerticalScrollbarPosition(1);
        }


        context=getContext();
        movieList = new ArrayList<MovieModel>();
        for(String id:Utils.getMedia(context,MEDIATYPE)){
            if(id!=null && !id.isEmpty())
                id=id.trim();
            new FetchMovieData().execute(id);
        }

        return rootView;
    }

    public class FetchMovieData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String id= params[0];
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";
            try {
                String strUrl = "https://api.themoviedb.org/3/movie/"+id+
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
            fetchJSONMovie(s);
            if(progressBar.getVisibility()!=View.GONE)
                progressBar.setVisibility(View.GONE);
        }

        private void fetchJSONMovie (String response) {
            try{
                JSONObject jsonObject = new JSONObject(response);
                    MovieModel movie = new MovieModel();
                    movie.setId(jsonObject.getInt("id"));
                    movie.setTitle(jsonObject.getString("title"));
                    movie.setOverview(jsonObject.getString("overview"));
                    movie.setVoteAverage(jsonObject.getDouble("vote_average"));
                    movie.setPosterPath(jsonObject.getString("poster_path"));
                    movie.setVoteCount(jsonObject.getInt("vote_count"));
                    movie.setOverview(jsonObject.getString("overview"));
                    movie.setReleaseDate(jsonObject.getString("release_date"));
                    movie.setBackdropPath(jsonObject.getString("backdrop_path"));
                    movieGenreNames = new ArrayList<>();
                    JSONArray jsonArrayGenre = jsonObject.getJSONArray("genres");
                    for (int j = 0; j < jsonArrayGenre.length(); j++) {
                        JSONObject jsonObjectList = jsonArrayGenre.getJSONObject(j);
                        GenreModel genre = new GenreModel();
                        genre.setId(jsonObjectList.getInt("id"));
                        genre.setGenreName(jsonObjectList.getString("name"));
                        genreList.add(genre);
                    }

                        for (GenreModel list : genreList) {
                                movieGenreNames.add(list.getGenreName());
                            }
                    movie.setGenreList(movieGenreNames);
                    movieList.add(movie);
                 movieAdapterSmall.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
            notifyDataChange();
        }
    }
    public void notifyDataChange(){
        movieAdapterBig.notifyDataSetChanged();
        movieAdapterSmall.notifyDataSetChanged();
        movieAdapterGrid.notifyDataSetChanged();
    }
}
