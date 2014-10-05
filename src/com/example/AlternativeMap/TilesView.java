package com.example.AlternativeMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class TilesView extends View {


    private MapLogic map;
    private SmartPoint prevPosition = null;




    public TilesView(Context context) {
        super(context);

        Tile.setDirForFileSaving(context.getCacheDir().getAbsolutePath());

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        map = new MapLogic(this, new Point(displaymetrics.widthPixels, displaymetrics.heightPixels));

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (prevPosition == null) {
                float cx = event.getX();
                float cy = event.getY();
                prevPosition = new SmartPoint((int) cx, (int) cy);
            }


        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float cx = event.getX();
            float cy = event.getY();

            SmartPoint currPos = new SmartPoint((int) cx, (int) cy);
            SmartPoint delta = currPos.diff(prevPosition);
            map.update(delta);
            prevPosition = currPos;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            prevPosition = null;
        }

//        return super.onTouchEvent(event);
        return true;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        map.draw(canvas);
    }


}