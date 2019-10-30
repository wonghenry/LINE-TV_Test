package com.example.linetv_test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView;


public class TVAdapter extends BaseAdapter implements AbsListView.OnScrollListener, Filterable {

    private LayoutInflater mInflater;
    private ArrayList<TVBean> mList;
    private ArrayList<TVBean> newList;

    private ImageLoader mImageLoader;
    private int mStart, mEnd;
    public static String URLS[];
    private boolean mFirstIn;
    Context mContext;

    public TVAdapter(Context context, ArrayList<TVBean> data, ListView listView) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mList = data;
        mImageLoader = new ImageLoader(listView);
        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).get_thumb();
        }
        listView.setOnScrollListener(this);
        mFirstIn = true;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.tv_thumb = (ImageView) convertView.findViewById(R.id.thumb);
            holder.tv_name = (TextView) convertView.findViewById(R.id.name);
            holder.tv_created_at = (TextView) convertView.findViewById(R.id.created_at);
            holder.tv_rating = (TextView) convertView.findViewById(R.id.rating);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String url = mList.get(position).get_thumb();
        holder.tv_thumb.setTag(url);
        mImageLoader.showImages(holder.tv_thumb, url);


        Log.d("getView", "position : " + position);
        holder.tv_name.setText(mList.get(position).get_name());

        DateTimeFormatter desiredFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        OffsetDateTime dateTime = OffsetDateTime.parse(mList.get(position).get_created_at());
        holder.tv_created_at.setText("出版日期 : " + dateTime.format(desiredFormatter));

        holder.tv_rating.setText("評分 : " + String.format("%.1f", Float.parseFloat(mList.get(position).get_rating())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(mContext, TVInformation.class);

                Bundle bundle = new Bundle();
                bundle.putString("thumb_uri", mList.get(position).get_thumb());
                bundle.putString("name", mList.get(position).get_name());
                bundle.putString("created_at", mList.get(position).get_created_at());
                bundle.putString("total_views", mList.get(position).get_total_views());
                bundle.putString("rating", mList.get(position).get_rating());

                intent.putExtras(bundle);
                mContext.startActivity(intent);



            }
        });


        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString();
                Log.d("getFilter", "constraint : " + constraint);
                FilterResults searchResult = new FilterResults();
                ArrayList<TVBean> result = new ArrayList<TVBean>();
                if (newList == null)
                    newList = mList;
                if (constraint != null) {
                    if (newList != null && newList.size() > 0) {
                        for (final TVBean g : newList) {
                            if (g.get_name().contains(constraint.toString())) {
                                result.add(g);

                            }
                        }
                    }
                    searchResult.values = result;

                    Log.d("getFilter", "searchResult.values : " + searchResult.values);
                    Log.d("getFilter", "searchResult.size : " + searchResult.count);
                }
                return searchResult;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mList = (ArrayList<TVBean>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                    Log.d("getFilter", "publishResults : " + results.count);

                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;

    }

    class ViewHolder {
        public ImageView tv_thumb;
        public TextView tv_name, tv_created_at, tv_rating;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = mStart + visibleItemCount;

        if (mFirstIn && visibleItemCount > 0) {
            mImageLoader.loadImages(mStart, mEnd);
            mFirstIn = false;
        }
    }



    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {

            mImageLoader.loadImages(mStart, mEnd);

        } else {

            mImageLoader.cancelAllTasks();
        }

    }


}
