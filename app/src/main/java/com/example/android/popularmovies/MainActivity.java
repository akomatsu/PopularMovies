package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.ContentProvider.FavoritesContract;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Utils.APIUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String PREFS = "PopularMoviesPrefs";

    private static final String SORT_ORDER = "sort_order";

    public static final String ORDER_POPULAR = "popular";
    public static final String ORDER_TOP_RATED = "top_rated";
    public static final String ORDER_FAVORITES = "favorites";


    public static final String MOVIE_EXTRA = "movie";

    private MoviesAdapter adapter;
    private String sortOrder;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_popular:
                sortOrder = ORDER_POPULAR;
                getMovieList();
                return true;
            case R.id.menu_top_rated:
                sortOrder = ORDER_TOP_RATED;
                getMovieList();
                return true;
            case R.id.menu_favorites:
                sortOrder = ORDER_FAVORITES;
                getMovieList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Movie> movies = new ArrayList<>();
        adapter = new MoviesAdapter(this, movies);

        GridView gridView = (GridView) findViewById(R.id.gv_movie_list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Store current sort order on shared preferences
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SORT_ORDER, this.sortOrder);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve sort order from shared preferences
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        this.sortOrder = settings.getString(SORT_ORDER, ORDER_POPULAR);
        getMovieList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Movie movie = adapter.getItem(position);
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MOVIE_EXTRA, movie); // pass movie object to movie details screen
        startActivity(intent);
    }

    private void getMovieList() {
        if (sortOrder.equals(ORDER_FAVORITES)) {
            new FetchMoviesInfoFromProvider().execute();
        } else {
            new FetchMoviesInfoFromWeb().execute(sortOrder);
        }
    }


    /**
     *  Async task that retrieves the movie list from favorites provider
     */
    private class FetchMoviesInfoFromProvider extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            String[] projection = {
                    FavoritesContract.MovieEntry.TMDB_ID,
                    FavoritesContract.MovieEntry.TITLE,
                    FavoritesContract.MovieEntry.ORIGINAL_TITLE,
                    FavoritesContract.MovieEntry.POSTER_PATH,
                    FavoritesContract.MovieEntry.SYNOPSIS,
                    FavoritesContract.MovieEntry.USER_RATING,
                    FavoritesContract.MovieEntry.RELEASE_DATE
            };
            Cursor cursor = getContentResolver().query(
                    FavoritesContract.MovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );

            if (cursor == null) {
                Log.e("PopularMovies", "Error getting favorites!!");
                return null;
            }

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            try {
                if (cursor == null || cursor.getCount() == 0) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    Movie[] movies = new Movie[cursor.getCount()];
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToNext();
                        movies[i] = new Movie();
                        movies[i].setId(cursor.getLong(cursor.getColumnIndex(FavoritesContract.MovieEntry.TMDB_ID)));
                        movies[i].setTitle(cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.TITLE)));
                        movies[i].setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.ORIGINAL_TITLE)));
                        movies[i].setPosterPath(cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.POSTER_PATH)));
                        movies[i].setSynopsis(cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.SYNOPSIS)));
                        movies[i].setUserRating(cursor.getDouble(cursor.getColumnIndex(FavoritesContract.MovieEntry.USER_RATING)));
                        movies[i].setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.RELEASE_DATE)));
                        movies[i].setFavorite(true);
                    }
                    adapter.clear();
                    adapter.addAll(movies);
                    adapter.notifyDataSetChanged();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     *  Async task that retrieves the movie list from themoviedb servers
     */
    private class FetchMoviesInfoFromWeb extends AsyncTask<String, Void, String> {

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
        protected String doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            String listType = strings[0];
            try {
                if (!isOnline()) {
                    return null;
                }
                URL url = APIUtils.buildMovieListURL(listType);
                return APIUtils.getResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                return;
            }

            Movie[] movies = APIUtils.parseMovieJSON(response);
            adapter.clear();
            adapter.addAll(movies);
            adapter.notifyDataSetChanged();
        }
    }
}
