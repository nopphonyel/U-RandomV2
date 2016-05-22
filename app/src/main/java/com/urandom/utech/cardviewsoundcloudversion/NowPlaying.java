package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class NowPlaying extends Activity implements View.OnClickListener {

    private static final String TAG_PLAYING = new String("NowPlaying.class");
    private static final String TAG_BIND_SERVICE = new String("ServiceConnection");
    private static final int LOVE = 1 , UNLOVE = 0;
    private static final int SIZE = 620;
    private static RelativeLayout.LayoutParams coverLayoutParams;
    protected ImageButton loveBtn;
    protected static TextView trackTitle;
    protected static ImageView cover;
    protected static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21){
            super.setTheme(android.R.style.Theme_Material_Light_NoActionBar_TranslucentDecor);
        }
        setContentView(R.layout.activity_now_playing);

        loveBtn = (ImageButton) findViewById(R.id.loveButton);

        trackTitle = (TextView) findViewById(R.id.playing_track_title);
        cover = (ImageView) findViewById(R.id.playing_track_cover);
        context = getApplicationContext();

        if(ProgramStaticConstant.FAVORITE_TRACK.containsKey(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getTrackID())){
            loveBtn.setImageResource(R.mipmap.ic_action_love);
        }else{
            loveBtn.setImageResource(R.mipmap.ic_action_unlove);
        }
        updateComponent();
        addOnClick();
    }

    public static void updateComponent(){
        trackTitle.setText(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getSongTitle());
        Log.d(TAG_PLAYING , ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getArtWorkURL());
        Picasso.with(context).load(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getLargeArtWorkURL()).placeholder(R.drawable.default_cover).into(cover);
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
