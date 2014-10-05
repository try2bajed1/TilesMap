package com.example.AlternativeMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

public class BitmapController {



    // Броадкасты:
    public static final String PICTURE_LOADED = "PICTURE_LOADED";
    public static final String DRAWING_COMPLETED = "DRAWING_COMPLETED";

    // Содержание броадкастов:
    public static final String LOADED_PICTURE_URL = "LOADED_PICTURE_URL";
    public static final String PICTURE_PATH = "PICTURE_PATH";
    public static final String IMAGEVIEW_ID = "IMAGEVIEW_ID";
    public static final String IS_DEFAULT_BITMAP = "IS_DEFAULT_BITMAP";

    public static final int DRAW_ALL = 10;
    public static final int DRAW_FILL = 11;

    private int defaultImageRes;
    private Bitmap defaultBitmap;

    private boolean loaderIsReady;

    private LinkedHashMap<ImageView, LoadingQueueItem> queue;


    private BitmapController() {

        defaultImageRes = -1;
        defaultBitmap = null;
        queue = new LinkedHashMap<ImageView, BitmapController.LoadingQueueItem>();
        loaderIsReady = true;
    }



    /**
     * Переданное изображение будет выводится если ссылка на изображение
     * невалидная или null
     *
     * @param imageResId
     */
    public void setDefaultImageRes(int imageResId) {
        defaultImageRes = imageResId;
    }


    /**
     * Переданное изображение будет выводится если ссылка на изображение
     * невалидная или null
     *
     * @param bitmap
     */
    public void setDefaultBitmap(Bitmap bitmap) {
        defaultBitmap = bitmap;
    }

    public void drawBitmap(String path, String url, ImageView view) {

        view.setImageDrawable(null);

        // Сначала пробуем получить картинку из кэша
        Bitmap bitmap = CacheController.getInstance().getBitmapFromMemCache(path);

        // Если в кэше нету - запускаем загрузку изображения в отдельном потоке
        if (bitmap == null) {

            // Для соблюдения корректного порядка в очереди - в cлучае если view
            // для загрузки повторяется - устаревший дубликат удаляем
            if (queue.containsKey(view)) {
                queue.remove(view);
            }
            queue.put(view, new LoadingQueueItem(path, url, view));
            updateQueque();
        } else {
            makeDrawing(view, bitmap,  path);
        }

    }





    private void makeDrawing(ImageView imageView, Bitmap bitmap, String path) {

        imageView.setScaleType(ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap(bitmap);
    }




    @SuppressLint("NewApi")
    protected void updateQueque() {

        if (loaderIsReady && queue.size() > 0) {

            ImageView firstKey = (ImageView) queue.keySet().toArray()[0];
            LoadingQueueItem item = queue.get(firstKey);

            BitmapLoadTask bitmapLoadTask = new BitmapLoadTask(item.getImageViewWeakRef());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                bitmapLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getLocalPath(), item.getUrl());
            } else {
                bitmapLoadTask.execute(item.getLocalPath(), item.getUrl());
            }
        }
    }


    public void clearQueue() {
        queue.clear();
    }



    protected class BitmapLoadTask extends AsyncTask<Object, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            loaderIsReady = false;
        }

        private final WeakReference<ImageView> imageViewReference;
        private int reqHeight;
        private int reqWidth;
//        private Integer drawRules;

        private String url;
        private String path;



        public BitmapLoadTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            reqHeight = imageView.getHeight();
            reqWidth = imageView.getWidth();
        }



        @Override
        protected Bitmap doInBackground(Object... params) {

            Bitmap bitmap = null;
            path = (String) params[0];
            url = (String) params[1];

            // Сначала проверяем загружен ли файл на устройство
            File imageFile = new File(path);

            // Если файл существует - формируем изображение
            if (imageFile.exists()) {
                bitmap = BitmapFactory.decodeFile(path);
            } else  {
                // Если файла не существует - загружаем его (сначала под другим именем)
                InputStream input = null;
                File tempFile = new File(path + ".tmp");

                try {

                    HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
                    connection.setConnectTimeout(10000);
                    input = connection.getInputStream();
                    OutputStream output = new FileOutputStream(tempFile);

                    try {

                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {

                            if (!isCancelled()) {
                                output.write(buffer, 0, bytesRead);
                            } else {
                                try {
                                    output.close();
                                    tempFile.delete();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        // В случае удачной загрузки временный файл переименованный в обычный и получаем из него изображение
                        if (tempFile.exists()) {
                            tempFile.renameTo(imageFile);
                            bitmap = BitmapFactory.decodeFile(path);
                        }

                    } catch (Exception ex) {
                       Log.i("@", "exception!!! "+ex.toString());
                    } finally {
                        try {
                            output.close();
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception ex) {

                }
            }


            if (bitmap == null) {
                bitmap = defaultBitmap;
            } else {
                CacheController.getInstance().addBitmapToCache(path, bitmap);
            }

            return bitmap;
        }



        @Override
        protected void onPostExecute(Bitmap bitmap) {

            final ImageView imageView = imageViewReference.get();

            if (imageView != null && queue.containsKey(imageView)) {
                makeDrawing(imageView, bitmap,  path);
                queue.remove(imageView);
            }

            loaderIsReady = true;
            updateQueque();
        }


        @Override
        protected void onCancelled() {
            loaderIsReady = true;
        }


    }





    protected class LoadingQueueItem {

        private String path;
        private String url;
        private WeakReference<ImageView> imageViewReference;

        LoadingQueueItem(String path, String url, ImageView imageView) {
            this.path = path;
            this.url = url;
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        public String getLocalPath() {
            return path;
        }

        public String getUrl() {
            return url;
        }

        public ImageView getImageViewWeakRef() {
            return imageViewReference.get();
        }



    }

}
