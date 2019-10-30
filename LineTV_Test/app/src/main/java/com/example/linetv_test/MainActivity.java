package com.example.linetv_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Struct;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ListView mListView;
    private SearchView mSearchView;
    private TVAdapter adapter;
    private File file;
    private Boolean InternetFlag = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 100;
    private static String jsonURL = "https://static.linetv.tw/interview/dramas-sample.json";

    ArrayList<TVBean> newsBeanList = new ArrayList<TVBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission_check();
        internet_check();

        mListView = (ListView) findViewById(R.id.list);
        mSearchView = (SearchView) findViewById(R.id.searchView);

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setFocusable(false);
        mSearchView.clearFocus();
        mSearchView.setQueryHint("搜尋劇名");

        search_function();


        mListView.setTextFilterEnabled(true);

        new NewsAsyncTask().execute(jsonURL);
    }

    public void permission_check() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {

        }

    }

    public void internet_check() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            InternetFlag = false;

        } else {
            if (!info.isAvailable()) {

            } else {
                InternetFlag = true;

            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {


                }
                return;
        }
    }



    private void search_function() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("search_function", "TextSubmit : " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("search_function", "TextChange : " + newText);
                adapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    private ArrayList<TVBean> getJsonData(String url) {

        JsonObject jsonObject;
        Gson gson = new Gson();
        JsonArray jsonArray;
        file = new File(Environment.getExternalStorageDirectory(), "LINE_TV.json");

        if (InternetFlag == true) {
            try {

                Log.d("getJsonData", "has Internet");

                String jsonString = readStream(new URL(url).openStream());

                Log.d("getJsonData", "jsonString : " + jsonString);

                jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();

                jsonArray = jsonObject.getAsJsonArray("data");

                for (JsonElement i : jsonArray) {
                    TVBean TVBean = gson.fromJson(i, new TypeToken<TVBean>() {
                    }.getType());
                    newsBeanList.add(TVBean);
                }

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(jsonString.toString().getBytes());
                fos.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {

                Log.d("getJsonData", "No Internet");

                file = new File(Environment.getExternalStorageDirectory(), "LINE_TV.json");
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader bf = new BufferedReader(isr);

                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }

                fis.close();
                isr.close();
                bf.close();

                jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();

                jsonArray = jsonObject.getAsJsonArray("data");

                for (JsonElement i : jsonArray) {
                    TVBean TVBean = gson.fromJson(i, new TypeToken<TVBean>() {
                    }.getType());
                    newsBeanList.add(TVBean);
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return newsBeanList;
    }

    private String readStream(InputStream is) {
        InputStreamReader isReader;
        String result = "";
        String line = "";
        try {
            isReader = new InputStreamReader(is, "utf-8");
            BufferedReader buffReader = new BufferedReader(isReader);
            while ((line = buffReader.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    class NewsAsyncTask extends AsyncTask<String, Void, ArrayList<TVBean>> {

        @Override
        protected ArrayList<TVBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<TVBean> result) {
            super.onPostExecute(result);
            adapter = new TVAdapter(MainActivity.this, result, mListView);
            mListView.setAdapter(adapter);
        }
    }
}

