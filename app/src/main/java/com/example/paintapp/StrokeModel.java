package com.example.paintapp;

import android.graphics.Path;

public class StrokeModel {

    public int color;
    public int strokeWidth;
    public Path path;

    public StrokeModel(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
