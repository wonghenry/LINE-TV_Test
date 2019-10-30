package com.example.linetv_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TVInformation extends AppCompatActivity {

    private String get_name, get_thumb_uri, get_created_at, get_total_views, get_rating;
    private TextView mget_name, mget_created_at, mget_total_views, mget_rating;
    private ImageView mget_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_information);

        Bundle getBundle = this.getIntent().getExtras();


        get_thumb_uri = getBundle.getString("thumb_uri");
        get_name = getBundle.getString("name");
        get_created_at = getBundle.getString("created_at");
        get_total_views = getBundle.getString("total_views");
        get_rating = getBundle.getString("rating");

        Log.d("TVInformation_onCreate", "get name:" + get_name);
        Log.d("TVInformation_onCreate", "get thumb_uri:" + get_thumb_uri);

        mget_name = (TextView) findViewById(R.id.in_name);
        mget_image = (ImageView) findViewById(R.id.in_thumb);
        mget_created_at = (TextView) findViewById(R.id.in_created_at);
        mget_total_views = (TextView) findViewById(R.id.in_total_views);
        mget_rating = (TextView) findViewById(R.id.in_rating);

        mget_name.setText(get_name);

        DateTimeFormatter desiredFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        OffsetDateTime dateTime = OffsetDateTime.parse(get_created_at);
        mget_created_at.setText("出版日期 : " + dateTime.format(desiredFormatter));

        mget_total_views.setText("觀看次數 : " + String.format("%.1f", Float.parseFloat(get_total_views) / 10000) + "萬次觀看");

        mget_rating.setText("評分 : " + String.format("%.1f", Float.parseFloat(get_rating)));

        setImage();

    }

    public void setImage() {

        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params)
            {
                String url = params[0];
                return getBitmapFromURL(url);
            }

            @Override
            protected void onPostExecute(Bitmap result)
            {
                mget_image.setImageBitmap(result);
                super.onPostExecute(result);
            }
        }.execute(get_thumb_uri);

    }


    private static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

