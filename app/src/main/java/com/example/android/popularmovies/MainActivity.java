package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.popularmovies.Network.APIUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView tv = null;

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

        tv = (TextView) findViewById(R.id.tv_main);
        getMovieList("popular");
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
            String[] movies = APIUtils.parseJSON(response);

            if (movies != null) {
                String txt = "";
                for (String s: movies) {
                    txt += s + "\n";
                }

                tv.setText(txt);
            }
        }
    }
}
