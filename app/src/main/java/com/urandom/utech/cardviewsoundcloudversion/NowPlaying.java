package com.urandom.utech.cardviewsoundcloudversion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class NowPlaying extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

    }
}
