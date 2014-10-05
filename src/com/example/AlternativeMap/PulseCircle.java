package com.example.AlternativeMap;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PulseCircle extends View implements Runnable {

    private long time = 1;
    private float cx;
    private float cy;
    private float radius;
    private float radiusStep = 1;
    private float radiusMax = 500;
    private float radiusMin = 50;

    private Paint paint;

    public PulseCircle(Context context) {

        super(context);

        radius = 50;
        paint = new Paint();
        paint.setColor(Color.BLUE);

        setFocusableInTouchMode(true);
        setClickable(true);
        setLongClickable(true);

        Thread thread = new Thread(this);
        thread.start();

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.i(MainActivity.TAG, "down baby down !! ");
            cx = event.getX();
            cy = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.i(MainActivity.TAG, "SHAKE !!!");
            cx = event.getX();
            cy = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.i(MainActivity.TAG, "nao encoste !!");
        }

        return super.onTouchEvent(event);
    }


    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        cx = getWidth() / 2;
        cy = getHeight() / 2;
    }


    public void draw(Canvas canvas) {

        super.draw(canvas);
        canvas.drawCircle(cx, cy, radius, paint);
    }


    public void run() {
        while (true) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                Log.e(MainActivity.TAG, "interrupcao do run()");
            }
            update();
            postInvalidate();
        }
    }



    private void update() {
        radius += radiusStep;
        if (radius > radiusMax) {
            radiusStep = -1;
        } else {
            if (radius < radiusMin) {
                radiusStep = 1;
            }
        }
    }

}