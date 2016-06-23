package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

public class NowPlaying extends FragmentActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Serializable {

    private static final String TAG_PLAYING = "NowPlaying.class";
    private static final long serialVersionUID = 4212499091071602833L;
    public static boolean ableToUpdateComponent = false;

    protected static Context context;

    protected static ProgressBar progressBar;
    protected static SeekBar seekBar;

    protected static ImageView playBtn;
    protected ImageView nextBtn;
    protected ImageView backBtn;

    protected static TextView musicNowTime , musicDuration;

    protected static ViewPager pager;
    protected static FragmentPageAdapter pageAdapter;

    protected TrackObject trackManagement;

    static int currentPositionOfPlayer = 0;

    private Handler updateSeekBarHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= 21 && ViewConfiguration.get(this).hasPermanentMenuKey()) {
            super.setTheme(android.R.style.Theme_Material_Light_NoActionBar_TranslucentDecor);
            setContentView(R.layout.activity_now_playing);
        } else {
            setContentView(R.layout.activity_now_playing_for_kitkat);
        }*/
        setContentView(R.layout.activity_now_playing_for_kitkat);

        trackManagement = new TrackObject();

        pager = (ViewPager) findViewById(R.id.pager_playing);
        pageAdapter = new FragmentPageAdapter(getSupportFragmentManager(), FragmentPageAdapter.NOW_PLAYING_ACTIVITY);
        pager.setAdapter(pageAdapter);
        Log.d(TAG_PLAYING, "pager was created");

        playBtn = (ImageView) findViewById(R.id.playBtn);
        nextBtn = (ImageView) findViewById(R.id.next_btn);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progressBar = (ProgressBar) findViewById(R.id.wait_for_streaming);

        musicDuration = (TextView) findViewById(R.id.duration_text_view);
        musicNowTime = (TextView) findViewById(R.id.now_time_text_view);

        context = getApplicationContext();

        NowPlaying.ableToUpdateComponent = true;
        //seekBarUpdater.start();
        updateComponent();
        NowPlaying.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentPositionOfPlayer = MusicService.player.getCurrentPosition();
                if (MusicService.isPlaying()) {
                    //seekBar.setProgress((int) (System.currentTimeMillis() - timeOffset + currentOffset));
                    seekBar.setProgress(currentPositionOfPlayer);
                    musicNowTime.setText(getTimeFormat(currentPositionOfPlayer));
                }
                else if(!MusicService.isPlaying() && !MusicService.isStopped()){
                    currentPositionOfPlayer = MusicService.lastPosition;
                    seekBar.setProgress(currentPositionOfPlayer);
                    musicNowTime.setText("Pause at " + getTimeFormat(currentPositionOfPlayer));
                }
                updateSeekBarHandler.postDelayed(this, 120);
            }
        });
        addOnClick();
    }

    public static void updateComponent() {
        Log.e(TAG_PLAYING, "UPDATING COMPONENT");
        if (FragmentMusicDetail.FRAGMENT_WAS_CREATED)
            FragmentMusicDetail.updateComponent();
        //Check track are ready for streaming
        if (MusicService.imNotReadyForPlaying) {
            NowPlaying.playBtn.setVisibility(View.INVISIBLE);
            NowPlaying.progressBar.setVisibility(View.VISIBLE);
        } else {
            NowPlaying.playBtn.setVisibility(View.VISIBLE);
            NowPlaying.progressBar.setVisibility(View.GONE);
        }

        if (MusicService.isPlaying()) {
            playBtn.setImageResource(R.mipmap.pause_btn);
        } else {
            playBtn.setImageResource(R.mipmap.plat_btn);
        }

        if(FragmentQueue.WAS_CREATED)
            FragmentQueue.updateComponent();

        seekBar.setMax(Integer.parseInt(MusicService.playingTrack.getTrackMilisecond()));
        musicDuration.setText(getTimeFormat(Integer.parseInt(MusicService.playingTrack.getTrackMilisecond())));
    }

    protected void addOnClick() {
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == playBtn) {
            if (MusicService.isPlaying()) {
                ProgramStaticConstant.musicService.pauseMusic();
                updateComponent();
            } else {
                ProgramStaticConstant.musicService.unpauseMusic();
                updateComponent();
            }
        }
        if (v == nextBtn) {
            ProgramStaticConstant.musicService.fastForword();
            if (ProgramStaticConstant.getTrackPlayingNo() + 1 > ProgramStaticConstant.TRACK.size()) {
                finish();
            }
        }
        if (v == backBtn) {
            ProgramStaticConstant.musicService.backForword();
            if (ProgramStaticConstant.getTrackPlayingNo() < 0) {
                Toast.makeText(context, "This is a first track in list", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NowPlaying.ableToUpdateComponent = false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //MusicService.gotoMusic(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MusicService.gotoMusic(seekBar.getProgress());
        MusicService.lastPosition = seekBar.getProgress();
        updateComponent();
    }

    protected static String getTimeFormat(int timeAsMilli){
        StringBuffer stringBuffer = new StringBuffer();
        if(timeAsMilli / 3600000 >= 1){
            stringBuffer.append(timeAsMilli / 3600000);
            stringBuffer.append(":");
        }
        stringBuffer.append(String.format("%02d", timeAsMilli % 3600000 / 60000));
        stringBuffer.append(":");
        stringBuffer.append(String.format("%02d", (timeAsMilli % 60000) / 1000));
        String processedTime = new String(stringBuffer);
        return processedTime;
    }

}
