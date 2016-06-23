package com.urandom.utech.cardviewsoundcloudversion;

import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by nopphonyel on 6/13/16.
 */
public class FragmentRandom extends Fragment implements View.OnClickListener {

    public static final int ON_LOAD = 1, ERROR_LOAD = 2, LOAD_SUCCESS = 3;

    private static RecyclerView trackList;
    private RecyclerView.LayoutManager trackListLayoutPotrait, trackListLayoutLandscape;
    public static TrackListAdapter trackListAdapter;

    public static boolean WAS_CREATED = false;

    protected static TrackObject trackFetcher;

    public static MenuItem stopPlayingMusic;

    private static ProgressBar spinner;
    private static TextView loadingText;

    private static Button refreshBtn;
    private static TextView errorText;

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savBundle){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_main_list , container , false);

        loadingText = (TextView) rootView.findViewById(R.id.loading_text);
        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar);

        refreshBtn = (Button) rootView.findViewById(R.id.retry_btn);
        errorText = (TextView) rootView.findViewById(R.id.no_con);
        refreshBtn.setOnClickListener(this);

        stopPlayingMusic = (MenuItem) rootView.findViewById(R.id.stop_service);

        trackList = (RecyclerView) rootView.findViewById(R.id.list_track);
        trackListLayoutPotrait = new LinearLayoutManager(getActivity());
        trackListLayoutLandscape = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        trackFetcher = new TrackObject();

        //Check Orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            trackList.setLayoutManager(trackListLayoutPotrait);
        else trackList.setLayoutManager(trackListLayoutLandscape);

        trackListAdapter = new TrackListAdapter(getActivity() , ProgramStaticConstant.TRACK , TrackListAdapter.MODE_RANDOM);
        trackList.setAdapter(trackListAdapter);

        fetchRecentTrack();

        WAS_CREATED = true;
        return rootView;
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

    public static void fetchRecentTrack() {
        setVisibilityOfComponent(ON_LOAD);
        ProgramStaticConstant.TRACK.clear();
        trackFetcher.getTrack(TrackObject.GET_BY_POPULAR_CHART , ParamTrack.GenreList.ELECTRONIC , ParamTrack.KIND_NEW_AND_HOT);
        shuffleTrack();
    }

    private static void shuffleTrack() {
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
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.retry_btn) {
                fetchRecentTrack();
            }
        }

}

