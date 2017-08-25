package com.example.android.popularmovies.Utils;

import android.net.Uri;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class APIUtils {
    private static final String API_KEY = "";

    private static final String API_ROOT_URL = "http://api.themoviedb.org/3";
    private static final String MOVIE_POPULAR = "/movie/popular";
    private static final String MOVIE_TOP_RATED = "/movie/top_rated";
    private final static String API_POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    private static final String KEY_PARAMETER = "api_key";

    private static final String JSON_RESULTS_ROOT = "results";
    private static final String JSON_ID_FIELD = "id";
    private static final String JSON_TITLE_FIELD = "title";
    private static final String JSON_ORIGINAL_TITLE_FIELD = "original_title";
    private static final String JSON_POSTER_PATH_FIELD = "poster_path";
    private static final String JSON_OVERVIEW_FIELD = "overview";
    private static final String JSON_VOTE_AVERAGE_FIELD = "vote_average";
    private static final String JSON_RELEASE_DATE_FIELD = "release_date";


    public static URL buildMovieListURL(String listType) {
        String tmdbURL = API_ROOT_URL;
        if (listType.equals(MainActivity.ORDER_POPULAR)) {
            tmdbURL += MOVIE_POPULAR;
        } else {
            tmdbURL += MOVIE_TOP_RATED;
        }

        Uri uri = Uri.parse(tmdbURL).buildUpon()
                .appendQueryParameter(KEY_PARAMETER, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Movie[] parseJSON(String json) {
        Movie[] movies = null;
        try {
            JSONObject fullJson = new JSONObject(json);
            JSONArray results = fullJson.getJSONArray(JSON_RESULTS_ROOT);
            movies = new Movie[results.length()];

            for (int i = 0; i < results.length(); i++) {
                JSONObject res = results.getJSONObject(i);

                movies[i] = new Movie();
                movies[i].setId(res.getLong(JSON_ID_FIELD));
                movies[i].setTitle(res.getString(JSON_TITLE_FIELD));
                movies[i].setOriginalTitle(res.getString(JSON_ORIGINAL_TITLE_FIELD));
                movies[i].setPosterPath(API_POSTER_URL + res.getString(JSON_POSTER_PATH_FIELD));
                movies[i].setSynopsis(res.getString(JSON_OVERVIEW_FIELD));
                movies[i].setUserRating(res.getDouble(JSON_VOTE_AVERAGE_FIELD));
                movies[i].setReleaseDate(res.getString(JSON_RELEASE_DATE_FIELD));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
