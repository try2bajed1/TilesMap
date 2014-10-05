package com.example.AlternativeMap;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

/**
 * Created with IntelliJ IDEA.
 * User: n.senchurin
 * Date: 04.10.2014
 * Time: 13:58
 */
public class TilesCache {

    private
    LruCache<Tile, Bitmap> tilesToBitmaps;
    private TilesLoader loader;

    public TilesCache() {
        loader = new TilesLoader(this);

        int systemMaxMemory = (int) Runtime.getRuntime().maxMemory() / (1024 * 1024);
        int maxCacheSize = systemMaxMemory / 2;

        tilesToBitmaps = new LruCache<Tile, Bitmap>(maxCacheSize) {
            @Override
            protected int sizeOf(Tile _, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                    return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
                } else {
                    return bitmap.getByteCount() / 1024;
                }
            }
        };
    }

    public void load(Tile tile) {
        int n = -19;
        synchronized (tilesToBitmaps) {
            n = tilesToBitmaps.size();
        }

        Log.i("@"," Tiles check cache for bitmap + CACHE CONTAINS <<< " + n);

        Bitmap bitmap = tilesToBitmaps.get(tile);
        if (bitmap != null) {
            Log.i("@"," bitmap already exists " + n);
            tile.setBitmap(bitmap);
        } else {
            Log.i("@"," loader CALL " + n);
            loader.load(tile);
        }
    }

    public void loadComplete(Tile tile, Bitmap bitmap) {
        tilesToBitmaps.put(tile, bitmap);
    }

    public void cancel(Tile tile) {
        if (tilesToBitmaps.get(tile) == null) {
            loader.cancel(tile);
        }
    }
}
