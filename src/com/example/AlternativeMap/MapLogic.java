package com.example.AlternativeMap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

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

    private static final SmartPoint tilesNum = new SmartPoint(100,100);
    private static final SmartPoint totalSize = tilesNum.mul(Tile.tileSize);


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
        globalTopLeftCorner = this.screenSize.diff(totalSize).div(2);
        updateTiles();
    }



    public void update(SmartPoint delta) {
        globalTopLeftCorner = globalTopLeftCorner.add(delta);
        if (globalTopLeftCorner.x > 0) globalTopLeftCorner.x = 0;
        if (globalTopLeftCorner.y > 0) globalTopLeftCorner.y = 0;

        SmartPoint totalDiff = totalSize.add(globalTopLeftCorner);
        if (totalDiff.x < 0) globalTopLeftCorner.x = -totalSize.x;
        if (totalDiff.y < 0) globalTopLeftCorner.y = -totalSize.y;

        updateTiles();
        reDraw();
    }



    public void reDraw() {

        synchronized (view) {
            view.postInvalidate();
        }
    }


    public void draw(Canvas canvas) {
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

        Iterator it = visibleTiles.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<SmartPoint, Tile> entry = (Map.Entry<SmartPoint, Tile>) it.next();

            if (keysToPoints.contains(entry.getKey())) {
                keysToPoints.remove(entry.getKey());
            } else {
                entry.getValue().remove();
                it.remove();
            }
        }

        for (SmartPoint key : keysToPoints) {
            visibleTiles.put(key, new Tile(this, key));
        }
    }
}
