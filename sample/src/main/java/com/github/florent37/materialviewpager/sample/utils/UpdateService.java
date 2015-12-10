package com.github.florent37.materialviewpager.sample.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


/**
 * Created by tomer on 22/10/14.
 */
public class UpdateService extends Service {

    Alarm alarm = new Alarm();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarm.SetAlarm(UpdateService.this);
        return START_STICKY;
    }


    public void onStart(Context context, Intent intent, int startId) {
        alarm.SetAlarm(context);
    }


}

