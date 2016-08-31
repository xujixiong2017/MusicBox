package com.example.administrator.musicbox;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

/**
 * Created by xujixiong on 2016/8/31.
 */

public class main_activity extends Activity implements View.OnClickListener {

    private Button exit;
    private Button list;
    private Button plyer;
    private Button last;
    private Button next;
    private CustomImageView imageView;
    private Intent intent1;
    private boolean is_plyer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        exit = (Button) findViewById(R.id.exit);
        list = (Button) findViewById(R.id.list);
        plyer = (Button) findViewById(R.id.plyer);
        last = (Button) findViewById(R.id.last);
        next = (Button) findViewById(R.id.next);
        imageView = (CustomImageView) findViewById(R.id.image);
        exit.setOnClickListener(this);
        list.setOnClickListener(this);
        plyer.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);

        //广播注册
        intent1 = new Intent();
        intent1.setAction("com.example.musicbox.plyerService$plyerBrocast");
        //服务注册
        if (!isServiceWork(this, "com.example.musicbox.plyerService$plyerBrocast")) {
            Intent intent = new Intent(this, plyerService.class);
            startService(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list:
                Intent intent = new Intent(main_activity.this, MusicList.class);
                startActivity(intent);
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.plyer:
                intent1.putExtra("statue", 0x00001);
                if (is_plyer) {
                    is_plyer = false;
                    plyer.setText("播放");
                } else {
                    is_plyer = true;
                    plyer.setText("暂停");
                }
                sendBroadcast(intent1);
                break;
            case R.id.last:
                intent1.putExtra("statue", 0X00003);
                sendBroadcast(intent1);
                break;
            case R.id.next:
                intent1.putExtra("statue", 0X00004);
                sendBroadcast(intent1);
                break;
        }
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
