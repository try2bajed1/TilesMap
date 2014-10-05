package com.example.AlternativeMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created with IntelliJ IDEA.
 * User: n.senchurin
 * Date: 04.10.2014
 * Time: 13:16
 */
public class Tile {


    public static final int tileSize = 256;
    private static final String remoteUrl = "http://b.tile.opencyclemap.org/cycle/16/"; //   33198/22539
    private static final String filePrefix = "tile-";
    private static final String imageExt = ".png";
    private static  String dirForSaving;

//    private static Bitmap defaultBitmap;
    private static TilesCache commonCache = new TilesCache();

    private MapLogic map;
    private SmartPoint index;
    private Bitmap bitmap = null;


//    public static void setDefaultBitmap(Bitmap bitmap) {
//        defaultBitmap = bitmap;
//    }
//


    public static void setDirForFileSaving(String path) {
        dirForSaving = path;
    }


    public Tile(MapLogic map, SmartPoint index) {
        this.map = map;
        this.index = index;
        commonCache.load(this);
    }


    public SmartPoint getIndex() {
        return index;
    }


    public void remove() {
        commonCache.cancel(this);
    }


    public String getPath() {
        return dirForSaving + "/" + filePrefix+ getNameSuffix("-");
    }


    public String getUrl() {
        return remoteUrl + getNameSuffix("/");
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        map.reDraw();
    }



    public void drawTo(Canvas canvas, Paint paint, SmartPoint globalTopLeftCorner) {
        if (bitmap != null) {
            SmartPoint pointSeekTo = index.mul(tileSize).add(globalTopLeftCorner);
            canvas.drawBitmap(bitmap, pointSeekTo.x, pointSeekTo.y, paint);
        }
    }

    private String getNameSuffix(String sep) {
        return "" + (33100 + index.x) + sep + (22500 + index.y) + imageExt;
    }
}
