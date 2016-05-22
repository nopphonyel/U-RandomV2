package com.urandom.utech.cardviewsoundcloudversion;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int ON_LOAD = 1, ERROR_LOAD = 2, LOAD_SUCCESS = 3;

    protected TrackObject trackFetcher;

    protected static int FAB_PLAYING = 3111 , FAB_SHUFFLE = 3112;
    protected static int FAB_STATE = 0;

    private static final String TAG = "MainActivity";
    private static RecyclerView trackList;
    private RecyclerView.LayoutManager trackListLayoutPotrait, trackListLayoutLandscape;
    public static TrackListAdapter trackListAdapter;

    private static FloatingActionButton nowPlayingBTN;

    private static ProgressBar spinner;
    private static TextView loadingText;

    private static Button refreshBtn;
    private static TextView errorText;

    private Intent playIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_drawer);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_toolbar);

        nowPlayingBTN = (FloatingActionButton) findViewById(R.id.shuffle_btn);
        nowPlayingBTN.setOnClickListener(this);

        loadingText = (TextView) findViewById(R.id.loading_text);
        spinner = (ProgressBar) findViewById(R.id.progressBar);

        refreshBtn = (Button) findViewById(R.id.retry_btn);
        errorText = (TextView) findViewById(R.id.no_con);
        refreshBtn.setOnClickListener(this);

        trackListAdapter = new TrackListAdapter(this, ProgramStaticConstant.TRACK);

        trackList = (RecyclerView) findViewById(R.id.list_track);
        trackListLayoutPotrait = new LinearLayoutManager(this);
        trackListLayoutLandscape = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        trackFetcher = new TrackObject();

        //Check Orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            trackList.setLayoutManager(trackListLayoutPotrait);
        else trackList.setLayoutManager(trackListLayoutLandscape);
        trackList.setAdapter(trackListAdapter);
        updateFloatingActionButton();
        fetchRecentTrack();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.e(ProgramStaticConstant.TAG_PLAYING , "onStart was called");
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, ProgramStaticConstant.musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(playIntent != null){
            unbindService(ProgramStaticConstant.musicConnection);
        }
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

    public void fetchRecentTrack() {
        setVisibilityOfComponent(ON_LOAD);
        ProgramStaticConstant.TRACK.clear();
        trackFetcher.getTrack(TrackObject.GET_BY_POPULAR_CHART);
        shuffleTrack();
    }

    private void shuffleTrack() {
        long seed = System.nanoTime();
        Collections.shuffle(ProgramStaticConstant.TRACK, new Random(seed));
        trackListAdapter.notifyDataSetChanged();
    }

    public static void setVisibilityOfComponent(int id) {
        if (id == LOAD_SUCCESS) {
            spinner.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
            refreshBtn.setVisibility(View.GONE);
            errorText.setVisibility(View.GONE);
            trackList.setVisibility(View.VISIBLE);
        } else if (id == ERROR_LOAD) {
            spinner.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
            trackList.setVisibility(View.GONE);
            refreshBtn.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.VISIBLE);
        } else if (id == ON_LOAD) {
            spinner.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            trackList.setVisibility(View.GONE);
            refreshBtn.setVisibility(View.GONE);
            errorText.setVisibility(View.GONE);
        }
    }

    public static void updateFloatingActionButton(){
        if(ProgramStaticConstant.isPlaying()){
            nowPlayingBTN.setImageResource(android.R.drawable.ic_media_play);
            FAB_STATE = FAB_PLAYING;
        }
        else {
            nowPlayingBTN.setImageResource(android.R.drawable.stat_notify_sync_noanim);
            FAB_STATE = FAB_SHUFFLE;
        }
    }

}
