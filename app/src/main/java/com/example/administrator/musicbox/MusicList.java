package com.example.administrator.musicbox;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujixiong on 2016/8/31.
 */
public class MusicList extends Activity implements AdapterView.OnItemClickListener {
    private List<Map<String, Object>> mListData;
    private ListView listView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musiclist);
        init();
    }

    public void init() {
        mListData = new ArrayList<Map<String, Object>>();
        mListData = getData();
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new ListViewAdapter(mListData, MusicList.this));
        listView.setOnItemClickListener(this);
        intent = new Intent();
        intent.setAction("com.example.musicbox.plyerService$plyerBrocast");
    }

    public List<Map<String, Object>> getData() {
        Cursor mAudioCursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.TITLE);
        // 循环输出歌曲的信息
        List<Map<String, Object>> mListData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mAudioCursor.getCount(); i++) {
            mAudioCursor.moveToNext();
            // 找到歌曲标题和总时间对应的列索引
            int indexTitle = mAudioCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);//歌名
            int indexARTIST = mAudioCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);//艺术家
            int indexALBUM = mAudioCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);//专辑
            int indexUri = mAudioCursor.getColumnIndex(MediaStore.Audio.Media.DATA); //文件路径

            String url = mAudioCursor.getString(indexUri);  //文件路径
            String strTitle = mAudioCursor.getString(indexTitle);
            String strARTIST = mAudioCursor.getString(indexARTIST);
            String strALBUM = mAudioCursor.getString(indexALBUM);

            HashMap<String, Object> nowMap = new HashMap<String, Object>();
            nowMap.put("Title", strTitle);//歌名
            nowMap.put("Artist", strARTIST);//艺术家
            nowMap.put("Album", strALBUM);//专辑
            nowMap.put("Uri", url);
            mListData.add(nowMap);
        }
        return mListData;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        intent.putExtra("statue", 0x00006);
        intent.putExtra("position", position);
        sendBroadcast(intent);
        Intent intent1 = new Intent(this, main_activity.class);
        startActivity(intent1);
    }
}
