package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.Data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviesAdapter extends ArrayAdapter<Movie> {

    private final static String API_POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);
        if (convertView == null) {
            convertView =
                    LayoutInflater.from(getContext()).inflate(R.layout.movie_list_item, parent, false);
        }

        ImageView poster = convertView.findViewById(R.id.iv_poster);
        Picasso.with(getContext()).load(API_POSTER_URL + movie.getPosterPath()).into(poster);
        return convertView;
    }
}
