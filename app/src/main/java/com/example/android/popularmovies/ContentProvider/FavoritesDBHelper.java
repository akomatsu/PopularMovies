package com.example.android.popularmovies.ContentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavoritesContract.MovieEntry.TABLE_MOVIES + "(" + FavoritesContract.MovieEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesContract.MovieEntry.TMDB_ID + " TEXT NOT NULL, " +
                FavoritesContract.MovieEntry.TITLE + " TEXT NOT NULL, " +
                FavoritesContract.MovieEntry.ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavoritesContract.MovieEntry.POSTER_PATH + " TEXT NOT NULL, " +
                FavoritesContract.MovieEntry.SYNOPSIS + " TEXT NOT NULL, " +
                FavoritesContract.MovieEntry.USER_RATING + " FLOAT NOT NULL, " +
                FavoritesContract.MovieEntry.RELEASE_DATE + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.MovieEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
            FavoritesContract.MovieEntry.TABLE_MOVIES + "'");
        onCreate(sqLiteDatabase);
    }
}
