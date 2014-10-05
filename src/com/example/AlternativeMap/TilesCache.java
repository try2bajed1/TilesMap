package com.example.AlternativeMap;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

/**
 * Created with IntelliJ IDEA.
 * User: n.senchurin
 * Date: 04.10.2014
 * Time: 13:58
 */
public class TilesCache {

    private LruCache<SmartPoint, Bitmap> pointsToBitmaps;
    private TilesLoader loader;

    public TilesCache() {

        loader = new TilesLoader(this);

        final int systemMaxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int maxCacheSize = systemMaxMemory / 2;

        pointsToBitmaps = new LruCache<SmartPoint, Bitmap>(maxCacheSize) {
            @Override
            protected int sizeOf(SmartPoint _, Bitmap bitmap) {
                int bytes;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                    bytes = bitmap.getRowBytes() * bitmap.getHeight();
                } else {
                    bytes = bitmap.getByteCount();
                }
                return bytes / 1024;
            }
        };
    }


    public void load(Tile tile) {
        Bitmap bitmap = pointsToBitmaps.get(tile.getIndex());
        if (bitmap != null) {
            tile.setBitmap(bitmap);
        } else {
            loader.load(tile);
        }
    }


    public void loadComplete(Tile tile, Bitmap bitmap) {
        pointsToBitmaps.put(tile.getIndex(), bitmap);
    }

    public void cancel(Tile tile) {
        if (pointsToBitmaps.get(tile.getIndex()) == null) {
            loader.cancel(tile);
        }
    }
}
