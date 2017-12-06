package com.example.android.themoviedb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.adapter.CreditAdapter;
import com.example.android.themoviedb.adapter.SimilarAdapter;
import com.example.android.themoviedb.model.CastModel;
import com.example.android.themoviedb.model.MovieModel;
import com.example.android.themoviedb.model.PeopleModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.example.android.themoviedb.R.id.toolbar;

public class PeopleActivity extends AppCompatActivity {

    String bio;
    TextView tvBio;
    TextView tvAka;
    TextView tvBirthday;
    TextView tvDeathday;
    TextView tvDied;
    TextView tvBirthplace;
    ImageView ivProfilePhoto;
    PeopleModel people = new PeopleModel();
    private List<CastModel> moviesList = new ArrayList<>();
    private RecyclerView rvMovies;
    private RecyclerView.LayoutManager layoutManager;
    private CreditAdapter creditAdapter;
    private static final int MEDIATYPE=2;
    boolean clicked = false;
    private Context context = this;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        tvBio = (TextView) findViewById(R.id.tv_biography);
        tvAka = (TextView) findViewById(R.id.tv_aka_text);
        tvBirthday = (TextView) findViewById(R.id.tv_birthday_text);
        tvBirthplace = (TextView) findViewById(R.id.tv_pob_text);
        tvDeathday = (TextView) findViewById(R.id.tv_died_text);
        tvDied = (TextView) findViewById(R.id.tv_died);
        ivProfilePhoto = (ImageView) findViewById(R.id.iv_profile_photo);

        tvDeathday.setVisibility(View.GONE);
        tvDied.setVisibility(View.GONE);

        rvMovies = (RecyclerView) findViewById(R.id.rv_people_movies);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvMovies.setLayoutManager(layoutManager);
        creditAdapter = new CreditAdapter(this, moviesList);
        rvMovies.setAdapter(creditAdapter);

        new FetchPeopleData().execute();
    }


    public class FetchPeopleData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONCast = "";

            try{
                String strUrl = " https://api.themoviedb.org/3/person/" +
                        String.valueOf(getIntent().getIntExtra("id", 0)) +"?api_key="+Utils.getApiKey() +
                        "&append_to_response=credits"+Utils.getLanguage();
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
                    strJSONCast = stringBuffer.toString();
                    return strJSONCast;
                }

            } catch (IOException e) {
                Log.e("Parse Error", e.toString());
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            FetchJSONCast(s);
        }

        private void FetchJSONCast(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);


                people.setBiography(jsonObject.getString("biography"));

                String littleBio = jsonObject.getString("biography");
                bio = littleBio.substring(0, Math.min(littleBio.length(), 250)) + "...";

                JSONArray jsonArrayAka = jsonObject.getJSONArray("also_known_as");
                if (jsonArrayAka != null && jsonArrayAka.length() > 0) {
                    tvAka.setText(jsonArrayAka.toString().replace("[", "").replace("]", "").replace("\"", "").replace(",", "\n"));
                } else {
                    tvAka.setText("-");
                }

                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy");
                String birthday = jsonObject.getString("birthday");
                Date birthDate = null;
                try {
                    birthDate = inputFormat.parse(birthday);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String birthdayFinal = outputFormat.format(birthDate);
                tvBirthday.setText(birthdayFinal);

                tvBirthplace.setText(jsonObject.getString("place_of_birth"));
                String deathday = jsonObject.getString("deathday");

                if (!deathday.equals("")) {

                    Date deathDate = null;
                    try {
                        deathDate = inputFormat.parse(deathday);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(deathDate!=null){
                        tvDied.setVisibility(View.VISIBLE);
                        tvDeathday.setVisibility(View.VISIBLE);
                        tvDeathday.setText(outputFormat.format(deathDate));
                    }

                }

                tvBio.setText(bio);
                Picasso.with(context).load("https://image.tmdb.org/t/p/h632" + jsonObject.getString("profile_path"))
                        .into(ivProfilePhoto);


                JSONObject jsonObjectCredits = jsonObject.getJSONObject("credits");
                JSONArray jsonArrayCast = jsonObjectCredits.getJSONArray("cast");

                for (int i = 0; i < jsonArrayCast.length(); i++) {
                    JSONObject jsonObjectCast = jsonArrayCast.getJSONObject(i);
                    CastModel cast = new CastModel();
                    cast.setId(jsonObjectCast.getInt("id"));
                    cast.setName(jsonObjectCast.getString("title"));
                    cast.setCharacter(jsonObjectCast.getString("character"));
                    cast.setDate(jsonObjectCast.getString("release_date"));
                    cast.setProfilePath(jsonObjectCast.getString("poster_path"));
                    moviesList.add(cast);
                }

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle(jsonObject.getString("name"));
                setSupportActionBar(toolbar);
                android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                assert actionBar != null;
                actionBar.setDisplayHomeAsUpEnabled(true);

                creditAdapter.notifyDataSetChanged();
            } catch (JSONException j) {
                Log.e("Error JSON", j.toString());
            }
        }
    }

    public boolean expandView(View v) {
        tvBio = (TextView) v.findViewById(R.id.tv_biography);

        if (!clicked) {
            tvBio.setText(people.getBiography());
            clicked = true;
        } else {
            tvBio.setText(bio);
            clicked = false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }
        // other menu select events may be present here
        if(item.getItemId()==R.id.favorite){
            Utils.saveMedia(context,item, String.valueOf(getIntent().getIntExtra("id",0)),MEDIATYPE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_main_menu, menu);

        MenuItem myFavoriteItem= menu.findItem(R.id.favorite);
        if(context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE).contains(String.valueOf(getIntent().getIntExtra("id",0))))
            myFavoriteItem.setIcon(R.drawable.ic_favorite_white_24dp);
        else{
            myFavoriteItem.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQuery(query, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String querySubmit) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("query", querySubmit);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

}
