package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.PeopleActivity;
import com.example.android.themoviedb.R;
import com.example.android.themoviedb.listener.CastClickListener;
import com.example.android.themoviedb.model.PeopleModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private Context context;
    private List<PeopleModel> peopleList;
    private TypeShow typeShow;

    public PeopleAdapter(Context context, List<PeopleModel> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
        this.typeShow=TypeShow.Big;
    }

    public PeopleAdapter(Context context, List<PeopleModel> peopleList,TypeShow type) {
        this.context = context;
        this.peopleList = peopleList;
        this.typeShow=type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_people_big, viewGroup, false);
        if(this.typeShow == TypeShow.Grid) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_people_grid, viewGroup, false);
        }
        else if(this.typeShow==TypeShow.Small)  {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_people_small, viewGroup, false);
        }
        else
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_people_big, viewGroup, false);
//        else {
//            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_row_big, viewGroup, false);
//            return new PersonViewHolder(v);
//        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PeopleModel people = peopleList.get(position);

        int number = position + 1;
        holder.tvNo.setText(Integer.toString(number) + ". ");
        holder.tvName.setText(people.getName());
        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + people.getProfilePath()).into(holder.ivFace);
        String popularityString = String.format("%,.2f",people.getPopularity());
        holder.tvPopularity.setText(popularityString);
        holder.setCastClickListener(new CastClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PeopleActivity.class);
                intent.putExtra("id", people.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNo;
        TextView tvName;
        ImageView ivFace;
        TextView tvPopularity;
        CastClickListener castClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNo = (TextView) itemView.findViewById(R.id.tv_no);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivFace = (ImageView) itemView.findViewById(R.id.iv_face);
            tvPopularity = (TextView) itemView.findViewById(R.id.tv_popularity);
            itemView.setOnClickListener(this);
        }

        public void setCastClickListener(CastClickListener castClickListener) {
            this.castClickListener = castClickListener;
        }

        @Override
        public void onClick(View view) {
            castClickListener.onClick(view);
        }
    }
}
