package com.example.android.popularmovies.Data;


public class Movie {

    private long id;
    private String title;
    private String posterPath;

    public Movie(long id, String title, String poster) {
        this.id = id;
        this.title = title;
        this.posterPath = poster;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
