package com.urandom.utech.cardviewsoundcloudversion;

import android.content.res.Configuration;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


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

    private Callback<ArrayList<SCTrack>> callback;
    private Callback<TrackObject> callbackTemp;

    StrictMode.ThreadPolicy networkPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_toolbar);

        StrictMode.setThreadPolicy(networkPolicy);

        nowPlayingBTN = (FloatingActionButton) findViewById(R.id.shuffle_btn);
        nowPlayingBTN.setOnClickListener(this);

        loadingText = (TextView) findViewById(R.id.loading_text);
        spinner = (ProgressBar) findViewById(R.id.progressBar);

        refreshBtn = (Button) findViewById(R.id.retry_btn);
        errorText = (TextView) findViewById(R.id.no_con);
        refreshBtn.setOnClickListener(this);

        //allTrackList = new ArrayList<SCTrack>();
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

        callback = new Callback<ArrayList<SCTrack>>() {
            @Override
            public void success(ArrayList<SCTrack> tracks, Response response) {
                setVisibilityOfComponent(LOAD_SUCCESS);
                reloadTrack(tracks);
            }

            @Override
            public void failure(RetrofitError error) {
                setVisibilityOfComponent(ERROR_LOAD);
                Log.d(TAG, "<E!>: " + error);
            }
        };

        callbackTemp = new Callback<TrackObject>() {
            @Override
            public void success(TrackObject scTrackJsonObj, Response response) {
                setVisibilityOfComponent(LOAD_SUCCESS);
                initializeArrayTrack(scTrackJsonObj.getTrackList());
                reloadTrack(allTrackList);
            }

            @Override
            public void failure(RetrofitError error) {
                setVisibilityOfComponent(ERROR_LOAD);
                Log.d(TAG, "<E!> Error at Callback: " + error);
            }
        };

        fetchRecentTrack();
    }


    /**
     * Check orientation
     *
     * @param newConfig
     */
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
        //RestAdapter restAdapt = new RestAdapter.Builder().setEndpoint(Config.API_V2_URL).build();
        //SCServiceV2 scService  = restAdapt.create(SCServiceV2.class);
        //getSupportActionBar().setTitle("Recent Popular Tracks");
        //setVisibilityOfComponent(ON_LOAD);
        //scService.getPopularTrack(callbackTemp);
        //scService.getPopularTrack(callback);
        //scService.getRecentTracks(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), callback);
        //scService.getPopularTrack(callback);

        /*RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Config.API_URL).build();
        SCService scService = restAdapter.create(SCService.class);
        getSupportActionBar().setTitle("Recent Popular Tracks");
        setVisibilityOfComponent(ON_LOAD);
        scService.getSpecificTracks("Electronics",callback);*/

        setVisibilityOfComponent(ON_LOAD);
        trackFetcher.getTrack(TrackObject.GET_BY_POPULAR);
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

    /**
     * To load a track
     *
     * @param scTracks List of SoundCloud track
     */
    public void reloadTrack(ArrayList<SCTrack> scTracks) {
        allTrackList.clear();
        allTrackList.addAll(scTracks);
        trackListAdapter.notifyDataSetChanged();

        Log.d(TAG, allTrackList.toString());
    }

    /*public void reloadTrack2(SCTrack scTracks)
    {
        allTrackList.clear();
        allTrackList.add(scTracks);
        trackListAdapter.notifyDataSetChanged();
    }*/

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_btn) {
            fetchRecentTrack();
            //Log.d(TAG,new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        }
        if (v.getId() == R.id.shuffle_btn) {
            long seed = System.nanoTime();
            Collections.shuffle(SCTrackList.TRACK, new Random(seed));
            trackListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Initialize Array list track
     * Call this inside Callback
     */
    public void initializeArrayTrack(JSONArray jsonArray){
        SCTrack tempForInsertToArray = new SCTrack();
        JSONObject jsonPoint;
        for(int i=0; i< jsonArray.length() ; i++){
            try {
                jsonPoint = jsonArray.getJSONObject(i);

                tempForInsertToArray.setSongTitle(jsonPoint.getString("title"));
                tempForInsertToArray.setArtWorkURL(jsonPoint.getString("artwork_url"));
                tempForInsertToArray.setGenre(jsonPoint.getString("genre"));
                tempForInsertToArray.setDuration(jsonPoint.getString("duration"));
                tempForInsertToArray.setTrackURL(jsonPoint.getString("uri"));
                tempForInsertToArray.setUser(jsonPoint.getJSONObject("user"));

                allTrackList.add(tempForInsertToArray);
            }catch (JSONException e) {
                Log.d(TAG, "Error at initializing Array");
            }
        }
    }
}
