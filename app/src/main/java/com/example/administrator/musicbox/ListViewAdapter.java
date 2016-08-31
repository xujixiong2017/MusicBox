package com.example.administrator.musicbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by xujixiong on 2016/8/31.
 */
public class ListViewAdapter extends BaseAdapter {
    private List<Map<String, Object>> mListData = null;
    private Context context;

    public ListViewAdapter(List<Map<String, Object>> mListData, Context context) {
        this.mListData = mListData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_list, null);
        ListViewHodler viewHodler = new ListViewHodler();
        viewHodler.Title = (TextView) convertView.findViewById(R.id.title);
        viewHodler.Artist = (TextView) convertView.findViewById(R.id.artist);
        if (mListData != null) {
            viewHodler.Title.setText(mListData.get(position).get("Title").toString());
            viewHodler.Artist.setText(mListData.get(position).get("Artist").toString());
        }
        return convertView;
    }
}
