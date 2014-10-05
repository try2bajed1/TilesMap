package com.example.AlternativeMap;

import android.util.LruCache;

/**
 * Created with IntelliJ IDEA.
 * User: n.senchurin
 * Date: 04.10.2014
 * Time: 13:58
 */
public class TilesCache {
    LruCache<SmartPoint, Tile> allTiles;

    public TilesCache() {
        int systemMaxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
        int maxCacheSize = systemMaxMemory / 4;

        allTiles = new LruCache<SmartPoint, Tile>(maxCacheSize) {
            @Override
            protected int sizeOf(SmartPoint key, Tile tile) {
                return tile.sizeOf();
            }
        };
    }

    public Tile get(SmartPoint tileIndex) {
        Tile tile = allTiles.get(tileIndex);
        if (tile != null) {
            return tile;
        } else {
            Tile newTile = new Tile(tileIndex);
            allTiles.put(tileIndex, newTile);
            return newTile;
        }
    }

    public void erase(SmartPoint tileIndex) {
        allTiles.remove(tileIndex);
    }
}
