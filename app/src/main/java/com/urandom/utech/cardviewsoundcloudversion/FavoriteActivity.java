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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Random;

/**
 * Favorite activity
 * Created by nopphonyel on 5/23/16.
 */
public class FavoriteActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int ON_LOAD = 1, ERROR_LOAD = 2, LOAD_SUCCESS = 3;
    public static boolean FAVORITE_ACTIVITY_WAS_CREATED = false;
    protected TrackObject trackManagement;

    protected static int FAB_PLAYING = 3111, FAB_SHUFFLE = 3112;
    protected static int FAB_STATE = 0;

    private static final String TAG = "MainActivity";
    private static RecyclerView trackList;
    private RecyclerView.LayoutManager trackListLayoutPotrait, trackListLayoutLandscape;
    public static TrackListFavoriteAdapter favoriteTrackListAdapter;

    public static MenuItem stopPlayingMusic;

    private static FloatingActionButton nowPlayingBTN;

    private static ProgressBar spinner;
    private static TextView loadingText;

    private Intent playIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_favorite_activity_icon);
        getSupportActionBar().setTitle(" Favorite Track");

        nowPlayingBTN = (FloatingActionButton) findViewById(R.id.shuffle_btn);
        nowPlayingBTN.setOnClickListener(this);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        loadingText = (TextView) findViewById(R.id.loading_text);

        stopPlayingMusic = (MenuItem) findViewById(R.id.stop_service);

        favoriteTrackListAdapter = new TrackListFavoriteAdapter(this, ProgramStaticConstant.FAVORITE_TRACK);
        FAVORITE_ACTIVITY_WAS_CREATED = true;

        trackList = (RecyclerView) findViewById(R.id.list_track);
        trackListLayoutPotrait = new LinearLayoutManager(this);
        trackListLayoutLandscape = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        trackManagement = new TrackObject();

        //Check Orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            trackList.setLayoutManager(trackListLayoutPotrait);
        else trackList.setLayoutManager(trackListLayoutLandscape);

        trackList.setAdapter(favoriteTrackListAdapter);

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
        if (v.getId() == R.id.shuffle_btn) {
            if (FAB_STATE == FAB_SHUFFLE) {
                Collections.shuffle(ProgramStaticConstant.FAVORITE_TRACK);
                favoriteTrackListAdapter.notifyDataSetChanged();
            } else if (FAB_STATE == FAB_PLAYING) {
                startActivity(new Intent(this, NowPlaying.class));
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!MusicService.isPlaying()) {
            menu.findItem(R.id.stop_service).setEnabled(false);
        } else {
            menu.findItem(R.id.stop_service).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_track:
                shuffleTrack();
                getFavoriteTrack();
                return true;
            case R.id.stop_service:
                ProgramStaticConstant.musicService.stopMusic();
                Log.d(TAG, "Trying to stoping");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getFavoriteTrack() {
        ProgramStaticConstant.FAVORITE_TRACK.clear();
        trackManagement.getFavoriteTrack();
        shuffleTrack();
    }

    private void shuffleTrack() {
        long seed = System.nanoTime();
        Collections.shuffle(ProgramStaticConstant.TRACK, new Random(seed));
        favoriteTrackListAdapter.notifyDataSetChanged();
    }

    public static void setVisibilityOfComponent(int id , String extraReport){
        if(FAVORITE_ACTIVITY_WAS_CREATED) {
            if (id == LOAD_SUCCESS) {
                spinner.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                trackList.setVisibility(View.VISIBLE);
            } else if (id == ERROR_LOAD) {
                loadingText.setText("Error : " + extraReport);
            } else if (id == ON_LOAD) {
                spinner.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                loadingText.setText("Importing favorite track");
                trackList.setVisibility(View.GONE);
            }
        }
    }

    public static void updateFloatingActionButton() {
        if (MusicService.isPlaying()) {
            nowPlayingBTN.setImageResource(android.R.drawable.ic_media_play);
            FAB_STATE = FAB_PLAYING;
        } else {
            nowPlayingBTN.setImageResource(android.R.drawable.stat_notify_sync_noanim);
            FAB_STATE = FAB_SHUFFLE;
        }
    }
}
