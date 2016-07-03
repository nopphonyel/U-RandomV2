package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * This activity for a beautiful splash screen
 * Created by nopphonyel on 6/22/16.
 */
public class Splash extends Activity{

    private Handler handler;
    private Runnable run;

    long delay_time;
    long time = 800L;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        super.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar);
        setContentView(R.layout.splash_layout);

        handler = new Handler();
        run = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this , MainActivity.class));
                finish();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(run, delay_time);
        time = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(run);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}
