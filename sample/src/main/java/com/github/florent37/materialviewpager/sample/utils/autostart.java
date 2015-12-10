package com.github.florent37.materialviewpager.sample.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by tomer on 11/4/14.
 */
public class autostart extends BroadcastReceiver {
    public void onReceive(Context arg0, Intent arg1) {
        if (Resources.autoCheck(arg0)) {
            Intent intent = new Intent(arg0, UpdateService.class);
            arg0.startService(intent);
            Log.i("Autostart", "started");
        }
    }
}