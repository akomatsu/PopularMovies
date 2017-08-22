package com.example.android.popularmovies.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import com.squareup.picasso.Transformation;

public class ResizeTransformation implements Transformation {

    private Activity activity;
    private double targetWidth;

    public ResizeTransformation(Activity activity, double widthScale) {
        this.activity = activity;
        this.targetWidth = getDisplayWidth() * widthScale;
    }

    private int getDisplayWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
        int targetHeight = (int) (targetWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(source, (int)targetWidth, targetHeight, false);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "resizePosterTransformation";
    }
}
