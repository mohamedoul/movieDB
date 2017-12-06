package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.MovieActivity;
import com.example.android.themoviedb.R;
import com.example.android.themoviedb.listener.MovieClickListener;
import com.example.android.themoviedb.model.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{
    private TypeShow typeShow=TypeShow.Small;
    private Context context;
    private List<MovieModel> movieList;

    public MovieAdapter(Context context, List<MovieModel> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    public MovieAdapter(FragmentActivity activity, List<MovieModel> movieList, TypeShow type) {
        this.context = context;
        this.movieList = movieList;
        this.typeShow=type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_movies_small, viewGroup, false);
        if(this.typeShow == TypeShow.Grid) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_movies_grid, viewGroup, false);
        }
        else if(this.typeShow==TypeShow.Small)  {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_movies_small, viewGroup, false);
        }
        else{
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_movies_big, viewGroup, false);
        }
            //view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_people_big, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MovieModel movie = movieList.get(position);

        holder.tvTitle.setText(movie.getTitle());
        holder.tvVote.setText(Double.toString(movie.getVoteAverage()));
        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + movie.getPosterPath()).into(holder.ivPoster);


        final StringBuilder genres = new StringBuilder();
        Iterator<String> it = movie.getGenreList().iterator();
        if (it.hasNext()) {
            genres.append(it.next());
        }
        while (it.hasNext()) {
            genres.append(", ");
            genres.append(it.next());
        }
        holder.tvGenre.setText(genres);

        holder.setMovieClickListener(new MovieClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieActivity.class);
                intent.putExtra("id", movie.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvGenre;
        TextView tvVote;
        ImageView ivPoster;

        MovieClickListener movieClickListener;

        //, Context context, List<MovieModel> movies -> params for ViewHolder()
        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvGenre = (TextView) itemView.findViewById(R.id.tv_genre);
            tvVote = (TextView) itemView.findViewById(R.id.tv_vote);
            ivPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
//            tvVoteCount = (TextView) itemView.findViewById(R.id.tv_vote_count);

            itemView.setOnClickListener(this);
        }

        public void setMovieClickListener(MovieClickListener movieClickListener) {
            this.movieClickListener = movieClickListener;
        }

        @Override
        public void onClick(View view) {
            movieClickListener.onClick(view);
        }
    }
}
