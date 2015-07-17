package com.github.florent37.materialviewpager.sample.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.florent37.materialviewpager.sample.Activities.ViewUpdateActivity;
import com.github.florent37.materialviewpager.sample.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tomer.
 */
public class Resources extends Activity {
    public static Context context;
    public static ProgressDialog mProgressDialog;

    public static int DelayTimeUpdate(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("check_every", "30")) * 60000;
    }

    public static boolean autoCheck(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("auto_check", true);
    }

    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static String DeviceModel() {
        //return Build.PRODUCT;
        return "hammerhead";
    }

    public static void Download(Context context, String url) {
        mProgressDialog = new ProgressDialog(context);
        String message = context.getResources().getString(R.string.download_in_progress);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(context, url);
        downloadTask.execute(url);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    public static int ic_drawer_light() {
        return (R.drawable.ic_drawer);
    }

    public static int ic_drawer_dark() {
        return (R.drawable.ic_drawer_dark);
    }

    public static String getProp(String prop) {
        String value = "";
        try {
            Process p;
            p = new ProcessBuilder("/system/bin/getprop", prop).redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                value = line;
            }
            p.destroy();

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return value;
    }

    public static void installZipBOSS(Context context) throws IOException, InterruptedException {
        File zip = new File("/sdcard/ALOSP_updates/update.zip");
        if (zip.exists()) {
            Toast.makeText(context, "ZIP EXIST!", Toast.LENGTH_LONG).show();
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "/system/bin/sh"});
            p.waitFor();
            OutputStream os = p.getOutputStream();
            os.write("echo 'boot-recovery ' > /cache/recovery/command\n".getBytes());
            os.write("echo '--update_package=/sdcard/ALOSP_updates/update.zip' >> /cache/recovery/openrecoveryscript\n".getBytes());
            os.write("reboot\n\r".getBytes());
            os.flush();
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            powerManager.reboot("recovery");
        } else {
            Toast.makeText(context, "ZIP NOT EXIST!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    public static class ListAdapter extends BaseAdapter {
        public static String DEVICE, ROM_VERSION, ANDROID_VERSION, KERNEL;
        String[][] updates;
        Activity context;
        ListView lv;

        public ListAdapter(Activity context, String[][] update, ListView lv) {
            this.updates = update;
            this.lv = lv;
            this.context = context;
            DEVICE = getString(context, R.string.device);
            ROM_VERSION = getString(context, R.string.rom_version);
            ANDROID_VERSION = getString(context, R.string.android_version);
            KERNEL = getString(context, R.string.kernel);
        }


        @Override
        public int getCount() {
            return updates[0].length + 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (position != 0) {
                convertView = context.getLayoutInflater().inflate(R.layout.item, parent, false);

                holder = new ViewHolder();
                holder.background = (CardView) convertView.findViewById(R.id.item_back);
                holder.backgroundWrapper = (LinearLayout) convertView.findViewById(R.id.item_back_wrapper);
                holder.version = (TextView) convertView.findViewById(R.id.text);
                holder.changelog = (TextView) convertView.findViewById(R.id.changelog);
                holder.download = (Button) convertView.findViewById(R.id.download);
                convertView.setTag(holder);
                try {
                    holder.background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent mIntent = new Intent(context, ViewUpdateActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putSerializable("list", updates);
                            mIntent.putExtras(mBundle);
                            mIntent.putExtra("position", position - 1);

                            context.getWindow().setExitTransition(new ChangeBounds());

                            ActivityOptions options = ActivityOptions
                                    .makeSceneTransitionAnimation(context, holder.background, "back");

                            context.startActivity(mIntent, options.toBundle());

                        }
                    });
                    holder.changelog.setText(updates[3][position - 1]);
                    holder.version.setText(context.getResources().getString(R.string.version) + " " + updates[0][position - 1]);
                    holder.download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Download(context, updates[2][position - 1]);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                convertView = context.getLayoutInflater().inflate(R.layout.system_info_item, parent, false);
                holder = new ViewHolder();
                holder.info = (TextView) convertView.findViewById(R.id.info);
                convertView.setTag(holder);
                String info = "\n" + DEVICE + ": " + Resources.getProp("ro.product.model") + "\n" + ROM_VERSION + ": " + Resources.getProp("ro.build.display.id").replaceAll("\n", "") + "\n" + ANDROID_VERSION + ": " + Resources.getProp("ro.build.version.release") + "\n" + KERNEL + ": " + System.getProperty("os.version");
                holder.info.setText(info);

            }
            return convertView;

        }

        private class ViewHolder {
            LinearLayout backgroundWrapper;
            CardView background;
            TextView version;
            TextView info;
            Button download;
            TextView changelog;
        }
    }

    public static class DownloadTask extends AsyncTask<String, Integer, String> {
        String link;
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context, String url) {
            this.context = context;
            this.link = url;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(link);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                File folder = new File(Environment.getExternalStorageDirectory() + "/ALOSP_updates");
                if (!folder.exists()) {
                    folder.mkdir();
                }

                output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/ALOSP_updates/update.zip");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            try {
                installZipBOSS(context);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ERROR", e.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}