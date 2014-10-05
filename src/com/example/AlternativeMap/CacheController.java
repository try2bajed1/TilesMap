package com.example.AlternativeMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;


public class CacheController {

	private LruCache<String, Bitmap> bitmapsCache;

	private static CacheController cacheUtils;


	public CacheController() {

		int systemMaxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
		int maxCacheSize = systemMaxMemory / 4;

		bitmapsCache = new LruCache<String, Bitmap>(maxCacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return CacheController.this.sizeOf(bitmap);
			}
		};
	}



	public static CacheController getInstance() {
		if (cacheUtils == null) {
			cacheUtils = new CacheController();
		}

		return cacheUtils;
	}

	/**
	 * Метод возвращает закэшированное изображение, асоциированное с ключом
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {
		return bitmapsCache.get(key);
	}

	/**
	 * Метод добавляет изображение в кэш (при условии отсутствия в кэше
	 * изображения с передаваемым ключом)
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			bitmapsCache.put(key, bitmap);
		}
	}
	
	/**
	 * Метод очищает кэш
	 * @return
	 */
	public void clearCache(){
		bitmapsCache.evictAll();
	}
	
	

	/**
	 * Метод возвращает размер изображения в байтах
	 * @param data
	 * @return
	 */
	@SuppressLint("NewApi")
	private int sizeOf(Bitmap data) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
			return (data.getRowBytes() * data.getHeight()) / 1024;
		} else {
			return data.getByteCount() / 1024;
		}
	}

}
