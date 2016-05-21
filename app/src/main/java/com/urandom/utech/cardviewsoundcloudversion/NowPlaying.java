package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

public class NowPlaying extends Activity implements View.OnClickListener {

    private static String TAG_PLAYING = new String("NowPlaying.class");
    private static final int LOVE = 1 , UNLOVE = 0;
    ImageButton loveBtn;
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21){
            super.setTheme(android.R.style.Theme_Material_Light_NoActionBar_TranslucentDecor);
        }
        setContentView(R.layout.activity_now_playing);

        loveBtn = (ImageButton) findViewById(R.id.loveButton);

        if(ProgramStaticConstant.FAVORITE_TRACK.containsKey(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getTrackID())){
            loveBtn.setImageResource(R.mipmap.ic_action_love);
        }else{
            loveBtn.setImageResource(R.mipmap.ic_action_unlove);
        }
        addOnClick();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            musicService.setList(ProgramStaticConstant.TRACK);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    protected void addOnClick(){
        loveBtn.setOnClickListener(this);
    }

    public void songPicked(View view){
        musicService.setSong(Integer.parseInt(view.getTag().toString()));
        musicService.playSong();
    }

    @Override
    public void onClick(View v) {
        if(v == loveBtn){
            //Click to add this track to favorite
            if(ProgramStaticConstant.FAVORITE_TRACK.containsKey(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getTrackID())){
                loveBtn.setImageResource(R.mipmap.ic_action_unlove);
                Log.e(TAG_PLAYING , ""+ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getTrackID());
                ProgramStaticConstant.FAVORITE_TRACK.remove(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getTrackID());
            }
            //Click to remove this track from favorite
            else {
                loveBtn.setImageResource(R.mipmap.ic_action_love);
                ProgramStaticConstant.FAVORITE_TRACK.put(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getTrackID() , ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()));
            }
        }
    }
}
