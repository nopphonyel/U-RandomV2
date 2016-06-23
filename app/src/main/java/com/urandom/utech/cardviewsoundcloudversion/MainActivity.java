package com.urandom.utech.cardviewsoundcloudversion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static boolean MAIN_ACTIVITY_WAS_CREATED = false;

    protected static int FAB_PLAYING = 3111, FAB_SHUFFLE = 3112;
    protected static int FAB_STATE = 0;

    private static final String TAG = "MainActivity";

    private static FloatingActionButton nowPlayingBTN;

    private ViewPager pager;
    private FragmentPageAdapter pagerAdapter;

    private Intent playIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_drawer);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_toolbar);
        getSupportActionBar().setTitle(" U-Random");
        if(getIntent().getAction() == ProgramStaticConstant.ForegroundServiceAction.ACTION_NOW_PLAYING){
            startActivity(new Intent(this , NowPlaying.class));
        }
        nowPlayingBTN = (FloatingActionButton) findViewById(R.id.shuffle_btn);
        nowPlayingBTN.setOnClickListener(this);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new FragmentPageAdapter(getSupportFragmentManager() , FragmentPageAdapter.MAIN_ACTIVITY);
        pager.setAdapter(pagerAdapter);

        MAIN_ACTIVITY_WAS_CREATED = true;
        updateFloatingActionButton();
        requestPermission();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.e(ProgramStaticConstant.TAG_PLAYING, "onStart was called");
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(ProgramStaticConstant.ForegroundServiceAction.ACTION_JUST_START);
            bindService(playIntent, ProgramStaticConstant.musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MAIN_ACTIVITY_WAS_CREATED = false;
        if (MusicService.isServiceExist()) {
            unbindService(ProgramStaticConstant.musicConnection);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shuffle_btn) {
            if (FAB_STATE == FAB_SHUFFLE) {
                if (pager.getCurrentItem() == 0) {
                    Collections.shuffle(ProgramStaticConstant.TRACK);
                    FragmentRandom.trackListAdapter.notifyDataSetChanged();
                }
                else if (pager.getCurrentItem() == 1){
                    Collections.shuffle(ProgramStaticConstant.FAVORITE_TRACK);
                    FragmentFavorite.favoriteTrackListAdapter.notifyDataSetChanged();
                }
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
        if (!MusicService.isPlaying() || MusicService.imNotReadyForPlaying) {
            menu.findItem(R.id.stop_service).setEnabled(false);
        } else {
            menu.findItem(R.id.stop_service).setEnabled(true);
        }

        if (!MusicService.isServiceExist()) {
            menu.findItem(R.id.real_stop_service).setEnabled(false);
        } else {
            menu.findItem(R.id.real_stop_service).setEnabled(true);
        }

        if(pager.getCurrentItem() == 1){
            menu.findItem(R.id.refresh_track).setVisible(false);
        }else if(pager.getCurrentItem() == 0){
            menu.findItem(R.id.refresh_track).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_track:
                if(pager.getCurrentItem() == 0){
                    FragmentRandom.fetchRecentTrack();
                }else{
                    Toast.makeText(this , "Action unavailable" , Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.stop_service:
                ProgramStaticConstant.musicService.stopMusic();
                Log.d(TAG, "Trying to stoping");
                return true;
            case R.id.real_stop_service:
                ProgramStaticConstant.musicService.stopRunning();
                unbindService(ProgramStaticConstant.musicConnection);
                ProgramStaticConstant.resetValue();
                FragmentRandom.trackListAdapter.notifyDataSetChanged();
                updateFloatingActionButton();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static void updateFloatingActionButton() {
        if (!MusicService.isStopped()) {
            nowPlayingBTN.setImageResource(android.R.drawable.ic_media_play);
            FAB_STATE = FAB_PLAYING;
        } else {
            nowPlayingBTN.setImageResource(android.R.drawable.stat_notify_sync_noanim);
            FAB_STATE = FAB_SHUFFLE;
        }
    }

    public static void updateComponent(){
        if(FragmentRandom.WAS_CREATED){
            FragmentRandom.trackListAdapter.notifyDataSetChanged();
        }
        if(FragmentFavorite.WAS_CREATED){
            FragmentFavorite.favoriteTrackListAdapter.notifyDataSetChanged();
        }
    }

    protected void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            }
        }
    }

}
