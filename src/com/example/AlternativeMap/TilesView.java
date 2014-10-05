package com.example.AlternativeMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

//public class TilesView extends View implements Runnable {
public class TilesView extends View {

//    private long time = 200; // !!!

    private MapLogic map;
    private SmartPoint prevPosition = null;

    private Thread thread;



    public TilesView(Context context) {
        super(context);

        Drawable myDrawable = getResources().getDrawable(R.drawable.item1);
        Bitmap bm = ((BitmapDrawable) myDrawable).getBitmap();
        Tile.setDefaultBitmap(bm);
        //Log.i("@","set folder "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
//        Tile.setSavedFilePath(Environment.getExternalStorageDirectory().getAbsolutePath());
        Tile.setSavedFilePath(context.getCacheDir().getAbsolutePath());

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        map = new MapLogic(this, new Point(displaymetrics.widthPixels, displaymetrics.heightPixels));
        Tile.setMap(map);

//        thread = new Thread(this);

//        setFocusableInTouchMode(true);
//        setClickable(true);
//        setLongClickable(true);


    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.i("@", "Start thread");
            if (prevPosition == null) {
                float cx = event.getX();
                float cy = event.getY();
                prevPosition = new SmartPoint((int) cx, (int) cy);
            }

//            if(thread.isAlive()) {
//                thread.start();
//            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float cx = event.getX();
            float cy = event.getY();

            SmartPoint currPos = new SmartPoint((int) cx, (int) cy);
            SmartPoint delta = currPos.diff(prevPosition);
            map.update(delta);
            prevPosition = currPos;

            Log.i("@","crd "+cx+" "+cy);
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.i("@", "Stop thread");
//            thread.interrupt();

            prevPosition = null;
        }

//        return super.onTouchEvent(event);
        return true;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        map.draw(canvas);
        Log.i("@","draw");
    }

//    @Override
//    public void run() {
//        while (true) {
//            try {
//                Thread.sleep(time);
//            } catch (InterruptedException e) {
//                Log.e(MainActivity.TAG, "interrupted_exception)");
//            }
//            postInvalidate();
//        }
//    }
}