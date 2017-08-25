package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Utils.ResizeTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviesAdapter extends ArrayAdapter<Movie> {


    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.movie_list_item, parent, false);

            // If we are creating a new ImageView, it's possible that image stored on cache
            // have the wrong dimensions. So we overwrite the cache with a new image with
            // correct dimensions.
            Picasso.with(getContext())
                    .load(movie.getPosterPath())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .transform(new ResizeTransformation((Activity)getContext()))
                    .into((ImageView)convertView);
        } else {
            Picasso.with(getContext())
                    .load(movie.getPosterPath())
                    .transform(new ResizeTransformation((Activity) getContext()))
                    .into((ImageView) convertView);
        }

        return convertView;
    }
}
