package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class NowPlaying extends Activity implements View.OnClickListener {

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

        if(ProgramStaticConstant.FAVORITE_TRACK.contains(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()))){
            loveBtn.setImageResource(android.R.drawable.star_on);
        }else{
            loveBtn.setImageResource(android.R.drawable.star_off);
        }
        addOnClick();
    }

    protected void addOnClick(){
        loveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == loveBtn){
            if(ProgramStaticConstant.FAVORITE_TRACK.contains(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()))){
                loveBtn.setImageResource(android.R.drawable.star_off);
                ProgramStaticConstant.FAVORITE_TRACK.remove(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()));
            }else {
                loveBtn.setImageResource(android.R.drawable.star_on);
                ProgramStaticConstant.FAVORITE_TRACK.add(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()));
            }
        }
    }
}
