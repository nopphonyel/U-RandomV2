package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

public class NowPlaying extends Activity implements View.OnClickListener {

    private static String TAG_PLAYING = new String("NowPlaying.class");
    private static final int LOVE = 1 , UNLOVE = 0;
    ImageButton loveBtn;

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

    protected void addOnClick(){
        loveBtn.setOnClickListener(this);
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
