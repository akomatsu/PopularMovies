package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Utils.APIUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String PREFS = "PopularMoviesPrefs";

    private static final String SORT_ORDER = "sort_order";
    private static final String SCROLL_POSITION = "scroll_position";

    private MoviesAdapter adapter;
    private GridView gridView;
    private String sortOrder;
    private int scrollPosition;

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
                sortOrder = "popular";
                getMovieList();
                return true;
            case R.id.menu_top_rated:
                sortOrder = "top_rated";
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

        gridView = (GridView) findViewById(R.id.gv_movie_list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SORT_ORDER, this.sortOrder);
        editor.putInt(SCROLL_POSITION, gridView.getFirstVisiblePosition());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        this.sortOrder = settings.getString(SORT_ORDER, "popular");
        this.scrollPosition = settings.getInt(SCROLL_POSITION, 0);
        getMovieList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Movie movie = adapter.getItem(position);
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    private void getMovieList() {
        new FetchMoviesInfo().execute(sortOrder);
    }

    private class FetchMoviesInfo extends AsyncTask<String, Void, String> {

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
                String response = APIUtils.getResponseFromHttpUrl(url);
                Log.e("FetchMoviesInfo", response);
                return response;
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

            Movie[] movies = APIUtils.parseJSON(response);
            adapter.clear();
            adapter.addAll(movies);
            adapter.notifyDataSetChanged();
            gridView.setSelection(scrollPosition); // TODO: not working....
        }
    }
}
