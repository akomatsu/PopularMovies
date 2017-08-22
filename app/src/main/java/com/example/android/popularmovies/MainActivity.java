package com.example.android.popularmovies;

import android.content.Intent;
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

    private MoviesAdapter adapter;
    private GridView gridView;

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
                getMovieList("popular");
                return true;
            case R.id.menu_top_rated:
                getMovieList("top_rated");
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

        getMovieList("popular");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Movie movie = adapter.getItem(position);
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    private void getMovieList(String mode) {
        new FetchMoviesInfo().execute(mode);
    }

    private class FetchMoviesInfo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            String listType = strings[0];

            try {
                URL url = APIUtils.buildMovieListURL(listType);
                String response = APIUtils.getResponseFromHttpUrl(url);
                Log.d("FetchMoviesInfo", response);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            Movie[] movies = APIUtils.parseJSON(response);
            adapter.clear();
            adapter.addAll(movies);
            adapter.notifyDataSetChanged();
            gridView.smoothScrollToPosition(0);
        }
    }
}
