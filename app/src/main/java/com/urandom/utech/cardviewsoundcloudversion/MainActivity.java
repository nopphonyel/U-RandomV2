package com.urandom.utech.cardviewsoundcloudversion;

import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener , SearchView.OnQueryTextListener {

    public static final int ON_LOAD = 1, ERROR_LOAD = 2, LOAD_SUCCESS = 3;

    protected TrackObject trackFetcher;

    private static final String TAG = "MainActivity";
    private ArrayList<SCTrack> allTrackList; //List of music
    private static RecyclerView trackList;
    private RecyclerView.LayoutManager trackListLayoutPotrait, trackListLayoutLandscape;
    private TrackListAdapter trackListAdapter;

    private FloatingActionButton nowPlayingBTN;

    private static ProgressBar spinner;
    private static TextView loadingText;

    private static Button refreshBtn;
    private static TextView errorText;

    private MenuItem  search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_toolbar);

        nowPlayingBTN = (FloatingActionButton) findViewById(R.id.shuffle_btn);
        nowPlayingBTN.setImageDrawable(null);
        nowPlayingBTN.setOnClickListener(this);

        loadingText = (TextView) findViewById(R.id.loading_text);
        spinner = (ProgressBar) findViewById(R.id.progressBar);

        refreshBtn = (Button) findViewById(R.id.retry_btn);
        errorText = (TextView) findViewById(R.id.no_con);
        refreshBtn.setOnClickListener(this);

        trackListAdapter = new TrackListAdapter(this, SCTrackList.TRACK);

        trackList = (RecyclerView) findViewById(R.id.list_track);
        trackListLayoutPotrait = new LinearLayoutManager(this);
        trackListLayoutLandscape = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        trackFetcher = new TrackObject();

        //Check Orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            trackList.setLayoutManager(trackListLayoutPotrait);
        else trackList.setLayoutManager(trackListLayoutLandscape);
        trackList.setAdapter(trackListAdapter);

        fetchRecentTrack();
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

    public void fetchRecentTrack() {
        setVisibilityOfComponent(ON_LOAD);
        SCTrackList.TRACK.clear();
        trackFetcher.getTrack(TrackObject.GET_BY_POPULAR_CHART);
        shuffleTrack();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_btn) {
            fetchRecentTrack();
        }
        if (v.getId() == R.id.shuffle_btn) {
            Toast.makeText(this , "Now playing button / shuffle when there is no playing music" , Toast.LENGTH_SHORT).show();
            trackListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        search = menu.findItem(R.id.search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.refresh_track :
                shuffleTrack();
                fetchRecentTrack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shuffleTrack()
    {
        long seed = System.nanoTime();
        Collections.shuffle(SCTrackList.TRACK, new Random(seed));
        trackListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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

}
