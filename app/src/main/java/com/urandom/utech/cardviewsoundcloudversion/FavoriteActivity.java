package com.urandom.utech.cardviewsoundcloudversion;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.Random;

/**
 * Created by nopphonyel on 5/23/16.
 */
public class FavoriteActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_favorite_activity_icon);
        updateFloatingActionButton();
        getFavoriteTrack();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            trackList.setLayoutManager(trackListLayoutPotrait);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            trackList.setLayoutManager(trackListLayoutLandscape);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_btn) {
            fetchRecentTrack();
        }
        if (v.getId() == R.id.shuffle_btn) {
            if(FAB_STATE == FAB_SHUFFLE){
                Collections.shuffle(ProgramStaticConstant.TRACK);
                trackListAdapter.notifyDataSetChanged();
            }
            else if(FAB_STATE == FAB_PLAYING){
                startActivity(new Intent(this , NowPlaying.class));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(!MusicService.isPlaying()){
            menu.findItem(R.id.stop_service).setEnabled(false);
        }else
        {
            menu.findItem(R.id.stop_service).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_track:
                shuffleTrack();
                fetchRecentTrack();
                return true;
            case R.id.stop_service:
                ProgramStaticConstant.musicService.stopMusic();
                Log.d(TAG , "Trying to stoping");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getFavoriteTrack() {
        setVisibilityOfComponent(ON_LOAD);
        ProgramStaticConstant.TRACK.clear();
        trackFetcher.getTrack(TrackObject.GET_BY_POPULAR_CHART , ParamTrack.GenreList.ELECTRONIC);
        shuffleTrack();
    }

    private void shuffleTrack() {
        long seed = System.nanoTime();
        Collections.shuffle(ProgramStaticConstant.TRACK, new Random(seed));
        trackListAdapter.notifyDataSetChanged();
    }

    public static void updateFloatingActionButton(){
        if(MusicService.isPlaying()){
            nowPlayingBTN.setImageResource(android.R.drawable.ic_media_play);
            FAB_STATE = FAB_PLAYING;
        }
        else {
            nowPlayingBTN.setImageResource(android.R.drawable.stat_notify_sync_noanim);
            FAB_STATE = FAB_SHUFFLE;
        }
    }
}
