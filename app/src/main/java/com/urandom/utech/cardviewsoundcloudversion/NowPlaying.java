package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class NowPlaying extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener , Serializable {

    private static final String TAG_PLAYING = new String("NowPlaying.class");
    private static final String TAG_BIND_SERVICE = new String("ServiceConnection");
    public static final String RESET_ALL = "resetAll", RESET_TIME_OFFSET = "resetTimeOffset";
    private static final int LOVE = 1, UNLOVE = 0;
    private static final int SIZE = 620;
    public static boolean ableToUpdateComponent = false;
    private static RelativeLayout.LayoutParams coverLayoutParams;
    protected static ImageView loveBtn;
    protected long currentPosition = 0;

    protected static TextView trackTitle;
    protected static TextView trackOwner;
    protected static ImageView cover;
    protected static Context context;

    protected static ProgressBar progressBar;
    protected static SeekBar seekBar;

    protected static ImageView playBtn;
    protected ImageView nextBtn;
    protected ImageView backBtn;

    protected TrackObject trackManagement;
    static long timeOffset = 0;
    static int currentOffset = 0;

    static int currentPositionOfPlayer = 0;

    protected Thread seekBarUpdater;

    private Handler updateSeekBarHandler = new Handler();
    protected String extraReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            super.setTheme(android.R.style.Theme_Material_Light_NoActionBar_TranslucentDecor);
            setContentView(R.layout.activity_now_playing);
        } else {
            setContentView(R.layout.activity_now_playing_for_kitkat);
        }

        trackManagement = new TrackObject();

        loveBtn = (ImageView) findViewById(R.id.loveButton);
        playBtn = (ImageView) findViewById(R.id.playBtn);
        nextBtn = (ImageView) findViewById(R.id.next_btn);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progressBar = (ProgressBar) findViewById(R.id.wait_for_streaming);

        trackTitle = (TextView) findViewById(R.id.playing_track_title);
        trackOwner = (TextView) findViewById(R.id.playing_track_owner);
        cover = (ImageView) findViewById(R.id.playing_track_cover);
        context = getApplicationContext();
        timeOffset = System.currentTimeMillis();
        NowPlaying.ableToUpdateComponent = true;
        updateComponent();
        //seekBarUpdater.start();
        NowPlaying.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentPositionOfPlayer = MusicService.player.getCurrentPosition();
                if (MusicService.isPlaying()) {
                    //seekBar.setProgress((int) (System.currentTimeMillis() - timeOffset + currentOffset));
                    seekBar.setProgress(currentPositionOfPlayer);
                }
                updateSeekBarHandler.postDelayed(this, 120);
            }
        });
        addOnClick();
    }

    public static void updateComponent() {
        Log.e(TAG_PLAYING, "UPDATING COMPONENT");
        //Set all component to match data
        trackTitle.setText(MusicService.playingTrack.getSongTitle());
        try {
            trackOwner.setText(MusicService.playingTrack.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Picasso.with(context).load(MusicService.playingTrack.getLargeArtWorkURL()).placeholder(R.drawable.default_cover).into(cover);

        //Check track is in a favorite collection
        if (ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.containsKey(MusicService.playingTrack.getTrackID())) {
            loveBtn.setImageResource(R.mipmap.ic_action_love);
        } else {
            loveBtn.setImageResource(R.mipmap.ic_action_unlove);
        }

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

        seekBar.setMax(Integer.parseInt(MusicService.playingTrack.getTrackMilisecond()));
    }

    protected void addOnClick() {
        loveBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loveBtn) {
            //Click to add this track to favorite
            if (ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.containsKey(MusicService.playingTrack.getTrackID())) {
                loveBtn.setImageResource(R.mipmap.ic_action_unlove);
                ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.remove(MusicService.playingTrack.getTrackID());
            }
            //Click to remove this track from favorite
            else {
                loveBtn.setImageResource(R.mipmap.ic_action_love);
                ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.put(MusicService.playingTrack.getTrackID(), MusicService.playingTrack);
            }
            trackManagement.saveFavoriteTrack();
            if(FavoriteActivity.FAVORITE_ACTIVITY_WAS_CREATED)
                FavoriteActivity.favoriteTrackListAdapter.notifyDataSetChanged();
        }
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

}
