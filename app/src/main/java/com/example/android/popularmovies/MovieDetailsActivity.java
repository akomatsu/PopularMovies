package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.ContentProvider.FavoritesContract;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.Review;
import com.example.android.popularmovies.Data.Trailer;
import com.example.android.popularmovies.Utils.APIUtils;
import com.example.android.popularmovies.Utils.ResizeTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {

    private Movie movie;
    private TrailerAdapter trailerAdapter;
    private RecyclerView trailersRV;
    private ReviewAdapter reviewAdapter;
    private RecyclerView reviewsRV;
    private Button favoriteBtn, unfavoriteBtn;
    private ImageView starIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        Bundle data = getIntent().getExtras();
        movie = data.getParcelable(MainActivity.MOVIE_EXTRA);

        TextView tv = (TextView) findViewById(R.id.tv_title);
        tv.setText(movie.getTitle());

        tv = (TextView) findViewById(R.id.tv_synopsis);
        tv.setText(movie.getSynopsis());

        tv = (TextView)  findViewById(R.id.tv_year);
        tv.setText(movie.getReleaseDate().substring(0, 4));

        tv = (TextView) findViewById(R.id.tv_user_rating);
        String rating = getResources().getString(R.string.user_rating);
        tv.setText(String.format(rating, movie.getUserRating()));

        ImageView iv = (ImageView) findViewById(R.id.iv_poster);

        // NO_CACHE: cached image (shown on movies list) have the wrong size for this screen
        // NO_STORE: without this, cache have to be redone for the entire movies list when we
        //          return to the movie list activity
        Picasso.with(this)
                .load(movie.getPosterPath())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .transform(new ResizeTransformation(this, 0.4))
                .into(iv);

        starIV = (ImageView) findViewById(R.id.iv_favorite_star);

        // Favorite button
        favoriteBtn = (Button) findViewById(R.id.btn_favorite);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                favoriteMovie();
            }
        });
        favoriteBtn.setVisibility(View.VISIBLE);
        starIV.setVisibility(View.GONE);

        // Unfavorite button
        unfavoriteBtn = (Button) findViewById(R.id.btn_unfavorite);
        unfavoriteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                unfavoriteMovie();
            }
        });
        unfavoriteBtn.setVisibility(View.GONE);


        if (isMovieFavorited(movie.getId())) {
            enableUnfavoriteBtn();
        } else {
            enableFavoriteBtn();
        }

        // TRAILERS
        trailersRV = (RecyclerView) findViewById(R.id.rv_trailers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        trailersRV.setLayoutManager(layoutManager);
        trailersRV.setHasFixedSize(false);
        Trailer placeholder = new Trailer();
        placeholder.setName(getResources().getString(R.string.loading_placeholder));
        placeholder.setPlaceholder(true);
        Trailer[] trailers = new Trailer[1];
        trailers[0] = placeholder;
        trailerAdapter = new TrailerAdapter(trailers);
        trailersRV.setAdapter(trailerAdapter);

        // REVIEWS
        reviewsRV = (RecyclerView) findViewById(R.id.rv_reviews);
        layoutManager = new LinearLayoutManager(this);
        reviewsRV.setLayoutManager(layoutManager);
        reviewsRV.setHasFixedSize(false);
        Review reviewPlaceholder = new Review();
        reviewPlaceholder.setAuthor("");
        reviewPlaceholder.setContent(getResources().getString(R.string.loading_placeholder));
        Review[] reviews = new Review[1];
        reviews[0] = reviewPlaceholder;
        reviewAdapter = new ReviewAdapter(reviews);
        reviewsRV.setAdapter(reviewAdapter);

        // start data retrieval
        getTrailersAndReviews();
    }

    public boolean isMovieFavorited(long tmdb_id) {
        String[] projection = {
                FavoritesContract.MovieEntry.TMDB_ID
        };

        String selection = FavoritesContract.MovieEntry.TMDB_ID + " = ?";
        String[] selectionArgs = {Long.toString(tmdb_id)};
        Cursor cursor = getContentResolver().query(
                FavoritesContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor == null) {
            Log.e("PopularMovies", "Error querying Content Providder for " + Long.toString(tmdb_id));
            return false;
        }

        int count = cursor.getCount();
        cursor.close();

        return (count != 0);
    }

    private void enableFavoriteBtn() {
        favoriteBtn.setVisibility(View.VISIBLE);
        unfavoriteBtn.setVisibility(View.GONE);
        starIV.setVisibility(View.GONE);
    }

    private void enableUnfavoriteBtn() {
        favoriteBtn.setVisibility(View.GONE);
        unfavoriteBtn.setVisibility(View.VISIBLE);
        starIV.setVisibility(View.VISIBLE);
    }

    private void favoriteMovie() {
        insertMovieIntoFavorites();
        enableUnfavoriteBtn();
    }

    private void unfavoriteMovie() {
        deleteMovieFromFavorites();
        enableFavoriteBtn();
    }

    private void insertMovieIntoFavorites() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(FavoritesContract.MovieEntry.TMDB_ID, movie.getId());
        contentValues.put(FavoritesContract.MovieEntry.TITLE, movie.getTitle());
        contentValues.put(FavoritesContract.MovieEntry.ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(FavoritesContract.MovieEntry.POSTER_PATH, movie.getPosterPath());
        contentValues.put(FavoritesContract.MovieEntry.SYNOPSIS, movie.getSynopsis());
        contentValues.put(FavoritesContract.MovieEntry.USER_RATING, movie.getUserRating());
        contentValues.put(FavoritesContract.MovieEntry.RELEASE_DATE, movie.getReleaseDate());

        getContentResolver().insert(
                FavoritesContract.MovieEntry.CONTENT_URI,
                contentValues
        );
    }

    private void deleteMovieFromFavorites() {
        String selectionClause = FavoritesContract.MovieEntry.TMDB_ID + " LIKE ?";
        String[] selectionArgs = {Long.toString(movie.getId())};

        getContentResolver().delete(
                FavoritesContract.MovieEntry.CONTENT_URI,
                selectionClause,
                selectionArgs
        );
    }

    private void reloadTrailersAdapter(Trailer[] trailers) {
        if (trailers.length == 0) {
            trailerAdapter.getItemAt(0).setName(getResources().getString(R.string.no_trailers));
            trailerAdapter.getItemAt(0).setPlaceholder(true);
        } else {
            trailersRV.removeViewAt(0);
            trailerAdapter.notifyItemRemoved(0);
            trailerAdapter.setTrailers(trailers);
        }
        trailerAdapter.notifyDataSetChanged();
    }

    private void reloadReviewsAdapter(Review[] reviews) {
        if (reviews.length == 0) {
            reviewAdapter.getItemAt(0).setContent(getResources().getString(R.string.no_reviews));
        } else {
            reviewsRV.removeViewAt(0);
            reviewAdapter.notifyItemRemoved(0);
            reviewAdapter.setReviews(reviews);
        }
        reviewAdapter.notifyDataSetChanged();
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }


    private void getTrailersAndReviews() {
        new FetchTrailersAndReviews().execute(movie.getId());
    }
    /**
     *  Async task that retrieve movie list from themoviedb servers
     */
    private class FetchTrailersAndReviews extends AsyncTask<Long, Void, String[]> {

        // Checks if there is a working internet connection
        // Code extracted from
        // https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
        private boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }

        @Override
        protected String[] doInBackground(Long... ids) {
            if (ids.length == 0) {
                return null;
            }

            long movieId = ids[0];
            try {
                if (!isOnline()) {
                    return null;
                }
                URL trailerUrl = APIUtils.buildTrailerListURL(movieId);
                URL reviewUrl = APIUtils.buildReviewListURL(movieId);

                String[] responses = new String[2];
                responses[0] = APIUtils.getResponseFromHttpUrl(trailerUrl);
                responses[1] = APIUtils.getResponseFromHttpUrl(reviewUrl);
                return responses;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] response) {
            if (response == null) {
                return;
            }

            Trailer[] trailers = APIUtils.parseTrailersJSON(response[0]);
            reloadTrailersAdapter(trailers);

            Review[] reviews = APIUtils.parseReviewsJSON(response[1]);
            reloadReviewsAdapter(reviews);
        }
    }
}
