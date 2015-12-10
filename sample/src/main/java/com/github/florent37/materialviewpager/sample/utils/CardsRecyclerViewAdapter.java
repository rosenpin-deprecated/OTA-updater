package com.github.florent37.materialviewpager.sample.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.sample.Activities.ViewUpdateActivity;
import com.github.florent37.materialviewpager.sample.R;
import com.panwrona.downloadprogressbar.library.DownloadProgressBar;
import com.parse.ParseObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;


/**
 * Created by florentchampigny on 24/04/15.
 */
public class CardsRecyclerViewAdapter extends RecyclerView.Adapter<CardsRecyclerViewAdapter.CardViewHolder> {

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    public static String DEVICE, ROM_VERSION, ANDROID_VERSION, KERNEL;
    List<ParseObject> contents;
    private Activity context;

    public CardsRecyclerViewAdapter(List<ParseObject> contents, Activity context) {
        this.contents = contents;
        this.context = context;
        Collections.reverse(this.contents);
        DEVICE = getString(context, R.string.device);
        ROM_VERSION = getString(context, R.string.rom_version);
        ANDROID_VERSION = getString(context, R.string.android_version);
        KERNEL = getString(context, R.string.kernel);
    }

    public static String getString(Activity context, int id) {
        return context.getResources().getString(id);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public CardsRecyclerViewAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.system_info_item, parent, false);
                return new CardsRecyclerViewAdapter.CardViewHolder(view) {

                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item, parent, false);
                return new CardsRecyclerViewAdapter.CardViewHolder(view) {

                };
            }
        }

        Log.d("Type is ", String.valueOf(viewType));
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:

                String info = "\n" + DEVICE + ": " + Resources.getProp("ro.product.model") + "\n" + ROM_VERSION + ": " + Resources.getProp("ro.build.display.id").replaceAll("\n", "") + "\n" + ANDROID_VERSION + ": " + Resources.getProp("ro.build.version.release") + "\n" + KERNEL + ": " + System.getProperty("os.version");
                holder.sysInfo.setText(info);
                break;
            case TYPE_CELL:
                holder.title.setText("Version " + contents.get(position).getString("VERSIONNUMBER"));
                holder.changelog.setText(contents.get(position).getString("CHANGELOG"));
                holder.title.setText("Version " + contents.get(position).getString("VERSIONNUMBER"));
                holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.downloadButton.playToSuccess();

                    }
                });
                holder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(context, ViewUpdateActivity.class);
                        mIntent.putExtra("title", contents.get(position).getString("VERSIONNUMBER"));
                        mIntent.putExtra("changelog", contents.get(position).getString("CHANGELOG"));
                        mIntent.putExtra("link", contents.get(position).getString("LINK"));

                        context.getWindow().setExitTransition(new ChangeBounds());

                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(context, holder.background, "back");

                        context.startActivity(mIntent, options.toBundle());
                    }
                });

                break;
        }

    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView title, changelog, sysInfo;
        DownloadProgressBar downloadButton;
        CardView background;

        CardViewHolder(final View itemView) {
            super(itemView);
            sysInfo = (TextView) itemView.findViewById(R.id.info);
            title = (TextView) itemView.findViewById(R.id.text);
            changelog = (TextView) itemView.findViewById(R.id.changelog);
            downloadButton = (DownloadProgressBar) itemView.findViewById(R.id.download);
            background = (CardView) itemView.findViewById(R.id.item_back);
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
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false

        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }

}