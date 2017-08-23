package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Utils.ResizeTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        Bundle data = getIntent().getExtras();
        Movie movie = data.getParcelable("movie");

        TextView tv = (TextView) findViewById(R.id.tv_title);
        tv.setText(movie.getTitle());

        tv = (TextView)findViewById(R.id.tv_original_title);
        tv.setText(movie.getOriginalTitle());

        tv = (TextView) findViewById(R.id.tv_synopsis);
        tv.setText(movie.getSynopsis());

        tv = (TextView) findViewById(R.id.tv_release_date);
        tv.setText(movie.getReleaseDate());

        tv = (TextView) findViewById(R.id.tv_user_rating);
        tv.setText(Double.toString(movie.getUserRating()) + "/10");

        ImageView iv = (ImageView) findViewById(R.id.iv_poster);
        Picasso.with(this)
                .load(movie.getPosterPath())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .transform(new ResizeTransformation(this, 0.4))
                .into(iv);
    }
}
