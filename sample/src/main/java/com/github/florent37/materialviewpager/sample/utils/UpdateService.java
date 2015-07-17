package com.github.florent37.materialviewpager.sample.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.florent37.materialviewpager.Utils;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        alarm.SetAlarm(UpdateService.this);
        return START_STICKY;
    }



    public void onStart(Context context,Intent intent, int startId)
    {
        alarm.SetAlarm(context);
    }


}

