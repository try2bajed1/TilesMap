package com.example.AlternativeMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: n.senchurin
 * Date: 04.10.2014
 * Time: 14:43
 */
public class TilesLoader {

    TilesCache tilesCache;

    HashSet<Tile> queue = new HashSet<Tile>();
    HashMap<Tile, LoadTask> tasksHM = new HashMap<Tile, LoadTask>();
    boolean isReady = true;


    public TilesLoader(TilesCache cache) {
        this.tilesCache = cache;
    }



    public void load(Tile tile) {
        queue.add(tile);
        updateQueue();
    }


    public void cancel(Tile tile) {
        queue.remove(tile);

        LoadTask task = tasksHM.get(tile);
        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true);
        }
    }

    private void updateQueue() {
        if (isReady && !queue.isEmpty()) {
            Tile firstTile = (Tile) queue.toArray()[0];

            LoadTask task = new LoadTask(firstTile);
            tasksHM.put(firstTile, task);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }
        }
    }

    private void remove(Tile tile) {
        queue.remove(tile);
        tasksHM.remove(tile);
    }

    public class LoadTask extends AsyncTask<Object, Void, Bitmap> {
        private final WeakReference<Tile> weakTile;

        public LoadTask(Tile tile) {
            weakTile = new WeakReference<Tile>(tile);
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bitmap = null;
            Tile tile = weakTile.get();

            if (tile != null) {
                bitmap = loadBitmap(tile.getPath(), tile.getUrl());
            }

            return bitmap;
        }



        @Override
        protected void onPreExecute() {
            isReady = false;
        }



        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Tile tile = weakTile.get();
            if (tile != null && queue.contains(tile)) {
                if (bitmap != null) {
                    queue.remove(tile);
                    tilesCache.loadComplete(tile, bitmap);
                    tile.setBitmap(bitmap);
                }
                remove(tile);
            }

            isReady = true;
            updateQueue();
        }

        @Override
        protected void onCancelled() {
            isReady = true;
        }

        private Bitmap loadBitmap(String path, String url) {
            File imageFile = new File(path);

            // Сначала проверяем загружен ли файл на устройство
            if (imageFile.exists()) {
                return BitmapFactory.decodeFile(path);
            } else {
                return downloadBitmap(imageFile, url);
            }
        }

        private Bitmap downloadBitmap(File imageFile, String url) {
            File tempFile = new File(imageFile.getPath() + ".tmp");
            Bitmap bitmap = null;
            try {

                HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
                connection.setConnectTimeout(10000);
                InputStream input = connection.getInputStream();
                OutputStream output = new FileOutputStream(tempFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    if (isCancelled()) {
                        output.close();
                        tempFile.delete();
                    } else {
                        output.write(buffer, 0, bytesRead);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // В случае удачной загрузки временный файл переименованный в обычный и получаем из него изображение
                if (tempFile.exists()) {
                    tempFile.renameTo(imageFile);
                    bitmap = BitmapFactory.decodeFile(imageFile.getPath());
                }
            }

            return bitmap;
        }
    }
}
