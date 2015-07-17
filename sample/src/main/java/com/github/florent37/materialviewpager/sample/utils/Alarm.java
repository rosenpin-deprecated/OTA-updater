package com.github.florent37.materialviewpager.sample.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomer on 11/4/14.
 */
public class Alarm extends BroadcastReceiver
{
    ArrayList<String> builds = new ArrayList<String>();
    int TimeToSleep;
    Context Context;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.Context = context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Parse.initialize(context, "LEuTfc5BQIC9E89F9u0J5ZfwZeGibiYTutcqK41j", "kQQ9CS4Wb7DzF4qooYodTHSmU1RFGfJNLthjzxHZ");
        TimeToSleep = Resources.DelayTimeUpdate(context);
        Log.d("Time is ",String.valueOf(TimeToSleep));
        checkForUpdate();


        // Put here YOUR code.

        wl.release();
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TimeToSleep, pi); // Millisec * Second * Minute
    }
    private void checkForUpdate() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Resources.DeviceModel());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                try {
                    for (int i = parseObjects.size() - 1; i > -1; i--) {
                        builds.add(parseObjects.get(i).getString("VERSIONNUMBER"));
                    }
                    Continue();
                } catch (Exception a) {
                    Toast.makeText(Context, "Error loading updates" + a, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void Continue() {
        if (newUpdateAvailable()) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(Context)
                            .setSmallIcon(android.R.drawable.stat_sys_upload)
                            .setContentTitle("Update is available!")
                            .setContentText("Touch to view update");
            NotificationManager mNotificationManager =
                    (NotificationManager) Context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }

    }

    private boolean newUpdateAvailable() {
        for (int i = 0; i < builds.size(); i++) {
            try {
                if (Integer.parseInt(Resources.getProp("ro.product.version_int")) < Integer.parseInt(builds.get(i))) {
                    return true;
                }
            } catch (Exception e) {
                Log.d("Unsupported rom", "You don't have the correct build.prop");
            }
        }
        return false;
    }

}
