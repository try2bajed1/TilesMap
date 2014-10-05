package com.example.AlternativeMap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: n.senchurin
 * Date: 04.10.2014
 * Time: 13:17
 */
public class MapLogic {

    private static final SmartPoint tilesNum = new SmartPoint(100, 100);
    private static final SmartPoint totalSize = tilesNum.mul(Tile.tileSize);

    private TilesCache cache = new TilesCache();

    private TilesView view;
    private SmartPoint screenSize;

    private final SmartPoint screenTopLeftCorner = new SmartPoint(0, 0);
    private SmartPoint globalTopLeftCorner;

    private HashMap<SmartPoint, Tile> visibleTiles = new HashMap<SmartPoint, Tile>();
    private Paint paint;


    public MapLogic(TilesView view, Point screenSize) {
        this.view = view;
        this.screenSize = new SmartPoint(screenSize);

        paint = getPaint();
        globalTopLeftCorner = this.screenSize.diff(totalSize).div(2);    // -12000 -11520
        updateTiles();
    }


    public void update(SmartPoint delta) {
        globalTopLeftCorner = globalTopLeftCorner.add(delta);
        updateTiles();
        reDraw();
    }



    public void reDraw() {

//        view.postInvalidate();

        synchronized (view) {
            Log.i("@", "### postInvalidate");
            view.postInvalidate();
        }
    }


    public void draw(Canvas canvas) {


        /*Tile tile = (Tile) visibleTiles.values().toArray()[0];
        if(tile !=null)
            tile.drawTo(canvas, paint, globalTopLeftCorner);*/



        for (Tile tile : visibleTiles.values()) {
            tile.drawTo(canvas, paint, globalTopLeftCorner);
        }
    }

    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        return paint;
    }

    private void updateTiles() {
        SmartPoint diff = screenTopLeftCorner.diff(globalTopLeftCorner);
        SmartPoint bottomRight = diff.add(screenSize).div(Tile.tileSize);
        SmartPoint topLeft = diff.div(Tile.tileSize);

        HashSet<SmartPoint> keysToPoints = new HashSet<SmartPoint>();
        for (int y = topLeft.y; y <= bottomRight.y; ++y) {
            for (int x = topLeft.x; x <= bottomRight.x; ++x) {
                keysToPoints.add(new SmartPoint(x, y));
            }
        }


/*
        for (Integer key : visibleTiles.keySet()) {
            if (keysToPoints.containsKey(key)) {
                keysToPoints.remove(key);
            } else {
                visibleTiles.remove(key);
            }
        }
*/

        Iterator it = visibleTiles.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<SmartPoint, Tile> entry = (Map.Entry<SmartPoint, Tile>) it.next();

            if (keysToPoints.contains(entry.getKey())) {
                keysToPoints.remove(entry.getKey());
            } else {
                //visibleTiles.remove(key);

//                cache.erase(entry.getKey());
                entry.getValue().cancel();
                it.remove();
            }
        }

        int n = 0;
        synchronized (visibleTiles) {
            n = visibleTiles.size();
        }

        if (n > 100) {
            Log.i("@"," reoved >>");
        }

        Log.i("@"," Hello %)) <<< " + n);

        for (SmartPoint key : keysToPoints) {
            visibleTiles.put(key, cache.get(key));
        }
    }
}
