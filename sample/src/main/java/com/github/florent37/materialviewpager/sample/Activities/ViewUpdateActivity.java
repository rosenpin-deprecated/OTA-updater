package com.github.florent37.materialviewpager.sample.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.github.florent37.materialviewpager.sample.R;
import com.github.florent37.materialviewpager.sample.utils.Resources;
import com.panwrona.downloadprogressbar.library.DownloadProgressBar;

/**
 * Created by tomer on 22/10/14.
 */
public class ViewUpdateActivity extends Activity {
    TextView version;
    TextView info;
    DownloadProgressBar download;
    TextView changelog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.view_update);

        getWindow().setSharedElementExitTransition(new AutoTransition());
        getWindow().setSharedElementEnterTransition(new AutoTransition());
        final Intent intent = getIntent();
        version = (TextView) findViewById(R.id.text);
        changelog = (TextView) findViewById(R.id.changelog);
        download = (DownloadProgressBar) findViewById(R.id.download);
        version.setText(getResources().getString(R.string.version) + " " + intent.getStringExtra("title"));
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources.Download(getApplicationContext(), intent.getStringExtra("link"));
            }
        });
        changelog.setText(intent.getStringExtra("changelog"));
    }
}
