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
    private TilesView tilesView;
    private SmartPoint screenSizePoint;
    private final SmartPoint screenTopLeftCorner = new SmartPoint(0, 0);
    private SmartPoint allTilesBitmapTopLeftCorner;


    private HashMap<SmartPoint, Tile> visibleTiles = new HashMap<SmartPoint, Tile>();
    private Paint paint;



    public MapLogic(TilesView tilesView, Point screenSizePoint) {
        this.tilesView = tilesView;
        this.screenSizePoint = new SmartPoint(screenSizePoint);

        paint = getPaint();
        allTilesBitmapTopLeftCorner = this.screenSizePoint.diff(totalSize).divide(2);
        updateTiles();
    }




    public void update(SmartPoint delta) {
        allTilesBitmapTopLeftCorner = allTilesBitmapTopLeftCorner.add(delta);
        if (allTilesBitmapTopLeftCorner.x > 0) allTilesBitmapTopLeftCorner.x = 0;
        if (allTilesBitmapTopLeftCorner.y > 0) allTilesBitmapTopLeftCorner.y = 0;

        SmartPoint totalDiff = totalSize.add(allTilesBitmapTopLeftCorner);
        if (totalDiff.x < 0) allTilesBitmapTopLeftCorner.x = -totalSize.x;
        if (totalDiff.y < 0) allTilesBitmapTopLeftCorner.y = -totalSize.y;

        updateTiles();
        reDraw();
    }



    public void reDraw() {
        synchronized (tilesView) {
            tilesView.postInvalidate();
        }
    }


    public void draw(Canvas canvas) {
        for (Tile tile : visibleTiles.values()) {
            tile.drawTo(canvas, paint, allTilesBitmapTopLeftCorner);
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
        SmartPoint delta = screenTopLeftCorner.diff(allTilesBitmapTopLeftCorner);
        SmartPoint bottomRightTileIndexes = delta.add(screenSizePoint).divide(Tile.tileSize);
        SmartPoint topLeftTileIndexes     = delta.divide(Tile.tileSize);

        HashSet<SmartPoint> keysToPoints = new HashSet<SmartPoint>();
        for (int y = topLeftTileIndexes.y; y <= bottomRightTileIndexes.y; ++y) {
            for (int x = topLeftTileIndexes.x; x <= bottomRightTileIndexes.x; ++x) {
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
