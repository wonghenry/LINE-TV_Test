package com.example.linetv_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ImageLoader {

    private ImageView mImageView;
    private String url;
    private LruCache<String, Bitmap> mCache;
    private ListView mListView;
    private Set<NewsAsyncTask> mTasks;

    public ImageLoader(ListView listView) {
        mListView = listView;
        mTasks = new HashSet<NewsAsyncTask>();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {

                return value.getByteCount();
            }
        };
    }

    public void addBitmapToCache(String url, Bitmap bitmap) {

        if (getBitmapFromCache(url) == null) {
            mCache.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mImageView.getTag().equals(url)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }

        }
    };


    public void showImageByThread(ImageView imageView, final String iconUrl) {
        mImageView = imageView;
        url = iconUrl;
        new Thread() {
            @Override
            public void run() {

                super.run();

                Bitmap bitmap = getBitmapFromUrl(iconUrl);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    public Bitmap getBitmapFromUrl(String urlString) {
        InputStream is = null;
        Bitmap bitmap;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            //Thread.sleep(1000);
            return bitmap;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if(is != null)
                    is.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }


    public void loadImages(int start, int end) {
        for (int i = start; i < end; i++) {
            String url = TVAdapter.URLS[i];

            Bitmap bitmap = getBitmapFromCache(url);

            if (bitmap == null) {
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTasks.add(task);

            } else {

                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void cancelAllTasks() {
        if (mTasks != null) {
            for (NewsAsyncTask task : mTasks) {
                task.cancel(false);
            }
        }
    }

    public void showImages(ImageView imageView, String iconUrl) {

        Bitmap bitmap = getBitmapFromCache(iconUrl);
        if (bitmap == null) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
        } else {
            imageView.setImageBitmap(bitmap);
        }

    }

    class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String mUrl;

        public NewsAsyncTask(String url) {

            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = getBitmapFromUrl(url);
            if (bitmap != null) {
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            super.onPostExecute(bitmap);

            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTasks.remove(this);
        }
    }


}
