package com.example.administrator.musicbox;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujixiong on 2016/8/31.
 */
public class plyerService extends Service {

    private boolean is_plyer = false;
    private int crrutposition = 0;
    private Handler handler = null;
    private MediaPlayer mediaPlayer = null;
    private List<Map<String, Object>> mListData;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.musicbox.plyerService$plyerBrocast");
        plyerBroadcast myBroadcast = new plyerBroadcast();
        registerReceiver(myBroadcast, intentFilter);
        mediaPlayer = new MediaPlayer();
        mListData = getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        Log.d("服务信息","死了");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(mListData.get(crrutposition).get("Uri").toString()));
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0X00001://播放
                        if (!is_plyer) {
                            mediaPlayer.start();
                            is_plyer = true;
                        } else {
                            mediaPlayer.pause();
                            is_plyer = false;
                        }
                        break;
                    case 0X00005://停止
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        break;
                    case 0X00003://上一曲
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        if (crrutposition > 0) {
                            is_plyer = true;
                            crrutposition--;
                            mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(mListData.get(crrutposition).get("Uri").toString()));
                            mediaPlayer.start();
                        } else {
                            Toast.makeText(getBaseContext(), "没有更多音乐", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0x00004://下一曲
                        crrutposition++;
                        mediaPlayer.stop();
                        if (crrutposition < mListData.size() - 1) {
                            is_plyer = true;
                            mediaPlayer.release();
                            mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(mListData.get(crrutposition).get("Uri").toString()));
                            mediaPlayer.start();
                        } else {
                            Toast.makeText(getBaseContext(), "没有更多音乐", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0x00006:
                        Log.d("服务信息", String.valueOf(crrutposition));
                        if (crrutposition >=0 && crrutposition <= mListData.size()) {
                            is_plyer = true;
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(mListData.get(crrutposition).get("Uri").toString()));
                            mediaPlayer.start();
                        }
                        break;
                }
            }
        };
        return super.onStartCommand(intent, flags, startId);
    }

    public class plyerBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            int statue = extras.getInt("statue");
            crrutposition = extras.getInt("position");
            handler.sendEmptyMessage(statue);
        }
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

}
