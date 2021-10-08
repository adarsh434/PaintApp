package com.example.paintapp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.paintapp.StrokeModel;

import java.util.ArrayList;

public class PaintView extends View {

    private Paint paint;
    private Path path;
    private int currentColor;
    private int strokeWidth;
    private ArrayList<StrokeModel> paths = new ArrayList<>();


    public PaintView(Context context) {
        super(context);
        init(null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();

        paint.setAntiAlias(true);
        paint.setDither(true);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        paint.setAlpha(0xff);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        currentColor = Color.RED;
        strokeWidth = 1;
    }

    public void setStroke(int stroke){ strokeWidth = stroke; }

    public void setColor(int color){ currentColor = color; }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        for (StrokeModel fp : paths) {
            paint.setColor(fp.color);
            paint.setStrokeWidth(fp.strokeWidth);
            canvas.drawPath(fp.path, paint);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent motionEvent){
        super.onTouchEvent(motionEvent);

        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                paths.add(new StrokeModel(currentColor, strokeWidth, path));
                path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();
        return true;
    }

}
