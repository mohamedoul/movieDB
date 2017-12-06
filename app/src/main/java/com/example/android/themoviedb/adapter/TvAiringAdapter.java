package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.R;
import com.example.android.themoviedb.TvActivity;
import com.example.android.themoviedb.listener.MovieClickListener;
import com.example.android.themoviedb.model.TvModel;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;


public class TvAiringAdapter extends RecyclerView.Adapter<TvAiringAdapter.ViewHolder> {

    private Context context;
    private List<TvModel> tvList;
    private TypeShow typeShow=null;
    public TvAiringAdapter(Context context, List<TvModel> tvList) {
        this.context = context;
        this.tvList = tvList;
        typeShow=null;
    }
    public TvAiringAdapter(Context context, List<TvModel> tvList, TypeShow typeShow) {
        this.context = context;
        this.tvList = tvList;
        this.typeShow=typeShow;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        if(typeShow==null)
         view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_tv_airing,viewGroup, false);
        else{
            if(this.typeShow == TypeShow.Grid) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_show_grid, viewGroup, false);
            }
            else if(this.typeShow==TypeShow.Small)  {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_show_small, viewGroup, false);
            }
            else{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_show_big, viewGroup, false);
            }
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TvModel tv = tvList.get(position);
        holder.tvTitle.setText(tv.getName());
        holder.tvVote.setText(Double.toString(tv.getVote()));
        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + tv.getPosterPath()).into(holder.ivPoster);


        final StringBuilder genres = new StringBuilder();
        Iterator<String> it = tv.getGenre_ids().iterator();
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
                Intent intent = new Intent(context, TvActivity.class);
                intent.putExtra("id", tv.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tvList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle;
        TextView tvGenre;
        TextView tvVote;
        ImageView ivPoster;
        MovieClickListener movieClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvGenre = (TextView) itemView.findViewById(R.id.tv_genre);
            tvVote = (TextView) itemView.findViewById(R.id.tv_vote);
            ivPoster = (ImageView) itemView.findViewById(R.id.iv_poster);

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
